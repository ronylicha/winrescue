package com.winrescue.usb

import android.content.Context
import android.util.Log
import com.topjohnwu.superuser.Shell
import com.winrescue.data.model.KeyAction
import com.winrescue.usb.HidKeyMap.KeyboardLayout
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestionnaire du peripherique HID clavier USB.
 *
 * Envoie des rapports HID de 8 octets sur /dev/hidg0 pour simuler
 * des frappes clavier sur la machine hote connectee en USB.
 *
 * Necessite un acces root (libsu) pour ecrire sur /dev/hidg0.
 */
@Singleton
class HidKeyboardManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var outputStream: FileOutputStream? = null
    private var hidDevicePath: String = "/dev/hidg0"

    companion object {
        private const val TAG = "HidKeyboardManager"
        val EMPTY_REPORT = ByteArray(8) { 0 }
        const val KEY_PRESS_MS = 50L
        const val KEY_RELEASE_MS = 50L
        const val DEFAULT_CHAR_DELAY_MS = 50L
    }

    /**
     * Verifie si le peripherique HID est disponible et accessible en ecriture.
     * Utilise une commande root pour tester l'existence et les permissions.
     */
    fun isHidAvailable(): Boolean {
        return try {
            val result = Shell.cmd("test -w $hidDevicePath && echo 'OK'").exec()
            result.isSuccess && result.out.any { it.trim() == "OK" }
        } catch (e: Exception) {
            Log.w(TAG, "Impossible de verifier le peripherique HID: ${e.message}")
            false
        }
    }

    /**
     * Ouvre un flux d'ecriture vers le peripherique HID.
     *
     * Tente d'abord un acces direct. Si ca echoue (permissions),
     * utilise une commande root pour ajuster les permissions puis reessaie.
     *
     * @return true si la connexion est etablie, false sinon.
     */
    fun connect(): Boolean {
        return try {
            // S'assurer que les permissions sont correctes via root
            Shell.cmd("chmod 666 $hidDevicePath").exec()
            outputStream = FileOutputStream(hidDevicePath)
            Log.i(TAG, "Connecte au peripherique HID: $hidDevicePath")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Echec de connexion au peripherique HID: ${e.message}")
            outputStream = null
            false
        }
    }

    /**
     * Ferme le flux d'ecriture et libere les ressources.
     */
    fun disconnect() {
        try {
            outputStream?.close()
        } catch (e: Exception) {
            Log.w(TAG, "Erreur lors de la fermeture du flux HID: ${e.message}")
        } finally {
            outputStream = null
            Log.i(TAG, "Deconnecte du peripherique HID")
        }
    }

    /**
     * Met a jour le chemin du peripherique HID (ex: /dev/hidg1).
     * Deconnecte le flux existant si ouvert.
     */
    fun updateDevicePath(path: String) {
        if (path != hidDevicePath) {
            disconnect()
            hidDevicePath = path
            Log.i(TAG, "Chemin HID mis a jour: $hidDevicePath")
        }
    }

    /**
     * Execute une action clavier HID selon le type de [KeyAction].
     *
     * @param action L'action a executer
     * @param inputs Variables de substitution pour les templates (cle -> valeur)
     * @param layout Le layout clavier a utiliser pour la conversion des caracteres
     */
    suspend fun sendKeyAction(
        action: KeyAction,
        inputs: Map<String, String>,
        layout: KeyboardLayout = KeyboardLayout.QWERTY_US
    ) {
        when (action) {
            is KeyAction.TypeString -> {
                val text = resolveTemplate(action.value, inputs)
                typeString(text, action.delayBetweenCharsMs, layout)
            }

            is KeyAction.PressKey -> {
                pressKey(action.key, action.modifier)
            }

            is KeyAction.KeyCombination -> {
                pressKeyCombination(action.keys)
            }

            is KeyAction.Wait -> {
                delay(action.ms)
            }

            is KeyAction.RepeatKey -> {
                repeatKey(action.key, action.count, action.delayBetweenMs)
            }

            is KeyAction.TemplateString -> {
                val resolved = resolveTemplate(action.template, inputs)
                typeString(resolved, action.delayBetweenCharsMs, layout)
            }

            is KeyAction.ShellCommand -> {
                // Gere par WizardViewModel.executeShellAction(), pas par le HID manager
            }
        }
    }

    // ---------------------------------------------------------------
    // Methodes privees d'envoi
    // ---------------------------------------------------------------

    /**
     * Tape une chaine de caracteres, caractere par caractere.
     * Chaque caractere est envoye comme un press + release avec un delai entre chaque.
     */
    private suspend fun typeString(text: String, delayMs: Long, layout: KeyboardLayout) {
        for (char in text) {
            val report = HidKeyMap.charToReport(char, layout)
            if (report != null) {
                sendReport(report)
                delay(KEY_PRESS_MS)
                sendReport(EMPTY_REPORT)
                delay(delayMs)
            } else {
                Log.w(TAG, "Caractere non supporte ignore: '$char' (0x${char.code.toString(16)})")
            }
        }
    }

    /**
     * Appuie sur une touche unique avec un modificateur optionnel.
     *
     * @param key Nom de la touche (ex: "ENTER", "a", "F5")
     * @param modifier Nom du modificateur optionnel (ex: "CTRL", "ALT")
     */
    private suspend fun pressKey(key: String, modifier: String?) {
        val report = ByteArray(8) { 0 }

        // Modifier
        if (modifier != null) {
            report[0] = HidKeyMap.modifierToByte(modifier)
        }

        // Keycode : d'abord chercher dans les touches speciales, sinon traiter comme caractere
        val keyCode = resolveKeyCode(key)
        if (keyCode != null) {
            report[2] = keyCode
        } else {
            Log.e(TAG, "Touche inconnue: '$key'")
            return
        }

        sendReport(report)
        delay(KEY_PRESS_MS)
        sendReport(EMPTY_REPORT)
        delay(KEY_RELEASE_MS)
    }

    /**
     * Appuie sur une combinaison de touches simultanees.
     * Les modificateurs sont combines dans l'octet 0, les keycodes dans les octets 2-7.
     *
     * @param keys Liste de noms de touches (ex: ["CTRL", "ALT", "DELETE"])
     */
    private suspend fun pressKeyCombination(keys: List<String>) {
        val report = ByteArray(8) { 0 }
        var modifierMask: Byte = 0x00
        val keyCodes = mutableListOf<Byte>()

        for (key in keys) {
            val modByte = HidKeyMap.modifierToByte(key)
            if (modByte != 0x00.toByte()) {
                // C'est un modificateur : combiner dans le masque
                modifierMask = (modifierMask.toInt() or modByte.toInt()).toByte()
            } else {
                // C'est une touche normale
                val keyCode = resolveKeyCode(key)
                if (keyCode != null) {
                    keyCodes.add(keyCode)
                } else {
                    Log.w(TAG, "Touche inconnue dans la combinaison: '$key'")
                }
            }
        }

        report[0] = modifierMask
        // Placer les keycodes dans les positions 2-7 (max 6)
        for (i in keyCodes.indices) {
            if (i > 5) {
                Log.w(TAG, "Trop de keycodes simultanes (max 6), les suivants sont ignores")
                break
            }
            report[2 + i] = keyCodes[i]
        }

        sendReport(report)
        delay(KEY_PRESS_MS)
        sendReport(EMPTY_REPORT)
        delay(KEY_RELEASE_MS)
    }

    /**
     * Repete l'appui sur une touche N fois avec un delai entre chaque repetition.
     */
    private suspend fun repeatKey(key: String, count: Int, delayBetweenMs: Long) {
        repeat(count) { iteration ->
            pressKey(key, null)
            if (iteration < count - 1) {
                delay(delayBetweenMs)
            }
        }
    }

    /**
     * Resout les placeholders {{key}} dans un template avec les valeurs fournies.
     *
     * @param template Le texte avec des placeholders (ex: "user: {{username}}")
     * @param inputs Les paires cle-valeur de substitution
     * @return Le texte avec les placeholders remplaces
     */
    private fun resolveTemplate(template: String, inputs: Map<String, String>): String {
        var result = template
        for ((key, value) in inputs) {
            result = result.replace("{{$key}}", value)
        }
        return result
    }

    /**
     * Resout un nom de touche en scancode HID.
     *
     * Cherche d'abord dans les touches speciales (ENTER, F1, etc.),
     * puis traite comme un caractere unique si applicable.
     */
    private fun resolveKeyCode(key: String): Byte? {
        // Touche speciale ?
        HidKeyMap.keyNameToCode(key)?.let { return it }

        // Caractere unique ?
        if (key.length == 1) {
            val pair = when {
                key[0] in 'a'..'z' -> Pair(0x00.toByte(), (0x04 + (key[0] - 'a')).toByte())
                key[0] in 'A'..'Z' -> Pair(0x02.toByte(), (0x04 + (key[0] - 'A')).toByte())
                key[0] in '1'..'9' -> Pair(0x00.toByte(), (0x1E + (key[0] - '1')).toByte())
                key[0] == '0' -> Pair(0x00.toByte(), 0x27.toByte())
                else -> null
            }
            return pair?.second
        }

        return null
    }

    /**
     * Ecrit un rapport HID de 8 octets sur le peripherique.
     *
     * @throws IllegalStateException si le flux n'est pas ouvert.
     */
    private fun sendReport(report: ByteArray) {
        val stream = outputStream
            ?: throw IllegalStateException("Flux HID non ouvert. Appelez connect() d'abord.")
        try {
            stream.write(report)
            stream.flush()
        } catch (e: Exception) {
            Log.e(TAG, "Erreur d'ecriture du rapport HID: ${e.message}")
            throw e
        }
    }
}
