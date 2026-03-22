package com.winrescue.data.root

import android.content.Context
import android.os.Build
import android.util.Log
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RootManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "RootManager"
        private const val DEFAULT_HID_PATH = "/dev/hidg0"

        // Appareils connus avec support HID natif (configfs F_HID compile)
        // Le UDC est specifique au SoC de chaque appareil
        private val KNOWN_DEVICES = mapOf(
            "Titan" to "11201000.usb0",       // Unihertz Titan
            "Titan 2" to "11201000.usb0",     // Unihertz Titan 2
            "Titan Pocket" to "11201000.usb0", // Unihertz Titan Pocket
            "Pixel" to "",                     // Google Pixel (UDC auto-detect)
            "Pixel 6" to "",
            "Pixel 7" to "",
            "Pixel 8" to "",
            "Pixel 9" to "",
            "OnePlus" to "",                   // OnePlus (UDC auto-detect)
        )

        /**
         * Verifie si l'appareil est un modele connu avec support HID.
         * Retourne le UDC specifique si connu, null sinon.
         */
        fun getKnownDeviceUdc(): String? {
            val model = Build.MODEL ?: return null
            for ((deviceName, udc) in KNOWN_DEVICES) {
                if (model.contains(deviceName, ignoreCase = true)) {
                    return udc
                }
            }
            return null
        }

        fun isKnownDevice(): Boolean = getKnownDeviceUdc() != null

        // Script configfs complet pour creer le gadget HID
        private val CONFIGFS_SETUP_SCRIPT = """
            # Charger le module
            modprobe libcomposite 2>/dev/null || true

            # Verifier si deja configure
            if [ -e /dev/hidg0 ]; then
                echo "HID_ALREADY_EXISTS"
                exit 0
            fi

            # Creer le gadget
            cd /sys/kernel/config/usb_gadget/ || exit 1
            mkdir -p g1 && cd g1
            echo 0x1d6b > idVendor
            echo 0x0104 > idProduct

            mkdir -p strings/0x409
            echo "WinRescue" > strings/0x409/product
            echo "WinRescue HID Keyboard" > strings/0x409/manufacturer

            mkdir -p configs/c.1/strings/0x409
            echo "HID Config" > configs/c.1/strings/0x409/configuration
            echo 120 > configs/c.1/MaxPower

            mkdir -p functions/hid.usb0
            echo 1 > functions/hid.usb0/protocol
            echo 1 > functions/hid.usb0/subclass
            echo 8 > functions/hid.usb0/report_length

            # HID Report Descriptor (Standard Keyboard)
            echo -ne '\x05\x01\x09\x06\xa1\x01\x05\x07\x19\xe0\x29\xe7\x15\x00\x25\x01\x75\x01\x95\x08\x81\x02\x95\x01\x75\x08\x81\x03\x95\x05\x75\x01\x05\x08\x19\x01\x29\x05\x91\x02\x95\x01\x75\x03\x91\x03\x95\x06\x75\x08\x15\x00\x25\x65\x05\x07\x19\x00\x29\x65\x81\x00\xc0' > functions/hid.usb0/report_desc

            ln -s functions/hid.usb0 configs/c.1/ 2>/dev/null || true

            # Activer le gadget
            UDC=$(ls /sys/class/udc/ | head -1)
            if [ -n "${'$'}UDC" ]; then
                echo "${'$'}UDC" > UDC
                echo "HID_SETUP_SUCCESS"
            else
                echo "HID_NO_UDC"
                exit 1
            fi
        """.trimIndent()

        // Script pour patch SELinux si necessaire
        private val SELINUX_PATCH_SCRIPT = """
            # Patch SELinux pour permettre l'acces a /dev/hidg0
            if command -v magiskpolicy &>/dev/null; then
                magiskpolicy --live "allow untrusted_app device chr_file { open read write ioctl }"
                echo "SELINUX_PATCHED_MAGISK"
            elif command -v ksud &>/dev/null; then
                ksud sepolicy patch "allow untrusted_app device chr_file { open read write ioctl }"
                echo "SELINUX_PATCHED_KSU"
            else
                # Tenter chmod direct
                chmod 666 /dev/hidg0 2>/dev/null
                echo "SELINUX_CHMOD_FALLBACK"
            fi
        """.trimIndent()
    }

    /**
     * Verification complete en 3 etapes :
     * 1. Root disponible ? (Shell.isAppGrantedRoot)
     * 2. /dev/hidg0 existe ?
     * 3. /dev/hidg0 accessible en ecriture ?
     */
    suspend fun checkRootStatus(): RootState = withContext(Dispatchers.IO) {
        try {
            // Forcer l'initialisation du shell avant de verifier
            // Shell.getShell() est bloquant et declenche la popup Magisk/KSU
            try {
                Shell.getShell()
            } catch (_: Exception) {
                return@withContext RootState.NoRoot
            }

            // Etape 1 : Verifier le root
            val rootGranted = Shell.isAppGrantedRoot()

            when (rootGranted) {
                null -> return@withContext RootState.NoRoot
                false -> return@withContext RootState.RootDenied
                true -> { /* continuer */ }
            }

            // Etape 2 : Verifier /dev/hidg0
            val hidExists = Shell.cmd("test -e $DEFAULT_HID_PATH && echo EXISTS").exec()
            if (!hidExists.out.contains("EXISTS")) {
                // Si appareil connu (Titan, Pixel, OnePlus...) → auto-configurer le HID
                if (isKnownDevice()) {
                    Log.i(TAG, "Appareil reconnu: ${Build.MODEL} — configuration HID automatique")
                    val setupResult = setupHidGadget()
                    if (setupResult is RootState.Ready) {
                        return@withContext setupResult
                    }
                    Log.w(TAG, "Auto-setup HID echoue: $setupResult")
                }
                return@withContext RootState.RootOnlyNoHid
            }

            // Etape 3 : Verifier ecriture
            val hidWritable = Shell.cmd("test -w $DEFAULT_HID_PATH && echo WRITABLE").exec()
            if (!hidWritable.out.contains("WRITABLE")) {
                // Tenter patch SELinux
                Shell.cmd(SELINUX_PATCH_SCRIPT).exec()
                // Re-verifier
                val retryWritable = Shell.cmd("test -w $DEFAULT_HID_PATH && echo WRITABLE").exec()
                if (!retryWritable.out.contains("WRITABLE")) {
                    return@withContext RootState.Error(
                        "HID existe mais non accessible en ecriture meme apres patch SELinux"
                    )
                }
            }

            RootState.Ready(DEFAULT_HID_PATH)
        } catch (e: Exception) {
            RootState.Error(e.message ?: "Erreur inconnue lors de la verification root")
        }
    }

    /**
     * Tente de configurer le gadget HID via configfs.
     * Strategie en 2 tentatives :
     * 1. D'abord essayer d'ajouter HID au gadget Android existant (g1)
     * 2. Si echec, creer un nouveau gadget from scratch
     */
    suspend fun setupHidGadget(): RootState = withContext(Dispatchers.IO) {
        try {
            // Tentative 1 : Ajouter HID au gadget existant (cas Titan 2, Pixel, etc.)
            val existingGadget = trySetupOnExistingGadget()
            if (existingGadget is RootState.Ready) {
                Log.i(TAG, "HID configure sur le gadget existant")
                return@withContext existingGadget
            }

            // Tentative 2 : Creer un nouveau gadget from scratch
            Log.i(TAG, "Gadget existant non exploitable, creation from scratch")
            val result = Shell.cmd(CONFIGFS_SETUP_SCRIPT).exec()
            val output = result.out.joinToString("\n")

            when {
                output.contains("HID_ALREADY_EXISTS") -> {
                    // Patch SELinux au cas ou
                    Shell.cmd(SELINUX_PATCH_SCRIPT).exec()
                    RootState.Ready(DEFAULT_HID_PATH)
                }
                output.contains("HID_SETUP_SUCCESS") -> {
                    // Patch SELinux
                    Shell.cmd(SELINUX_PATCH_SCRIPT).exec()
                    // Verifier que le device a ete cree
                    val exists = Shell.cmd("test -e $DEFAULT_HID_PATH && echo EXISTS").exec()
                    if (exists.out.contains("EXISTS")) {
                        RootState.Ready(DEFAULT_HID_PATH)
                    } else {
                        RootState.Error("Setup configfs reussi mais /dev/hidg0 non cree")
                    }
                }
                output.contains("HID_NO_UDC") -> {
                    RootState.Error(
                        "Aucun USB Device Controller trouve. Verifiez que le cable USB est branche."
                    )
                }
                else -> {
                    RootState.Error("Echec setup HID : ${result.err.joinToString("; ")}")
                }
            }
        } catch (e: Exception) {
            RootState.Error("Erreur setup HID : ${e.message}")
        }
    }

    /**
     * Tente d'ajouter la fonction HID au gadget Android existant (g1).
     * C'est la methode preferee car elle preserve ADB et les autres fonctions USB.
     * Fonctionne sur Unihertz Titan 2, Pixel, OnePlus et tout appareil
     * avec CONFIG_USB_CONFIGFS_F_HID=y compile dans le kernel.
     */
    private suspend fun trySetupOnExistingGadget(): RootState = withContext(Dispatchers.IO) {
        try {
            // Detecter le UDC (connu pour l'appareil ou auto-detect)
            val knownUdc = getKnownDeviceUdc()
            val udcCommand = if (!knownUdc.isNullOrEmpty()) {
                "echo $knownUdc"
            } else {
                "ls /sys/class/udc/ | head -1"
            }

            val script = """
                # Verifier que le gadget g1 existe
                if [ ! -d /sys/kernel/config/usb_gadget/g1 ]; then
                    echo "NO_EXISTING_GADGET"
                    exit 1
                fi

                # Detecter le nom de la fonction HID existante
                HID_FUNC=""
                for func in hid.gs0 hid.usb0 hid.0; do
                    if [ -d /sys/kernel/config/usb_gadget/g1/functions/${'$'}func ]; then
                        HID_FUNC=${'$'}func
                        break
                    fi
                done

                # Si pas de fonction HID, la creer
                if [ -z "${'$'}HID_FUNC" ]; then
                    mkdir -p /sys/kernel/config/usb_gadget/g1/functions/hid.usb0 2>/dev/null
                    if [ ${'$'}? -ne 0 ]; then
                        echo "CANNOT_CREATE_HID_FUNCTION"
                        exit 1
                    fi
                    HID_FUNC="hid.usb0"
                fi

                # Verifier si deja lie et actif
                if [ -e /dev/hidg0 ]; then
                    chmod 666 /dev/hidg0
                    echo "HID_ALREADY_ACTIVE"
                    exit 0
                fi

                # Detecter le UDC
                UDC=${'$'}($udcCommand)

                # Detacher le gadget pour modifier
                echo "" > /sys/kernel/config/usb_gadget/g1/UDC 2>/dev/null
                sleep 0.3

                # Configurer la fonction HID
                echo 1 > /sys/kernel/config/usb_gadget/g1/functions/${'$'}HID_FUNC/protocol
                echo 1 > /sys/kernel/config/usb_gadget/g1/functions/${'$'}HID_FUNC/subclass
                echo 8 > /sys/kernel/config/usb_gadget/g1/functions/${'$'}HID_FUNC/report_length

                # HID Report Descriptor (Standard Keyboard)
                printf '\x05\x01\x09\x06\xa1\x01\x05\x07\x19\xe0\x29\xe7\x15\x00\x25\x01\x75\x01\x95\x08\x81\x02\x95\x01\x75\x08\x81\x03\x95\x05\x75\x01\x05\x08\x19\x01\x29\x05\x91\x02\x95\x01\x75\x03\x91\x03\x95\x06\x75\x08\x15\x00\x25\x65\x05\x07\x19\x00\x29\x65\x81\x00\xc0' > /sys/kernel/config/usb_gadget/g1/functions/${'$'}HID_FUNC/report_desc

                # Detecter le config (b.1 ou c.1)
                CONFIG_DIR=""
                for cfg in b.1 c.1; do
                    if [ -d /sys/kernel/config/usb_gadget/g1/configs/${'$'}cfg ]; then
                        CONFIG_DIR=${'$'}cfg
                        break
                    fi
                done

                if [ -z "${'$'}CONFIG_DIR" ]; then
                    echo "NO_CONFIG_DIR"
                    exit 1
                fi

                # Lier la fonction HID
                ln -s /sys/kernel/config/usb_gadget/g1/functions/${'$'}HID_FUNC /sys/kernel/config/usb_gadget/g1/configs/${'$'}CONFIG_DIR/ 2>/dev/null

                # Reactiver le gadget
                echo "${'$'}UDC" > /sys/kernel/config/usb_gadget/g1/UDC
                sleep 0.5

                # Verifier + chmod
                if [ -e /dev/hidg0 ]; then
                    chmod 666 /dev/hidg0
                    echo "HID_SETUP_ON_EXISTING_OK"
                else
                    echo "HID_SETUP_ON_EXISTING_FAIL"
                    exit 1
                fi
            """.trimIndent()

            val result = Shell.cmd(script).exec()
            val output = result.out.joinToString("\n")
            Log.d(TAG, "trySetupOnExistingGadget output: $output")

            when {
                output.contains("HID_ALREADY_ACTIVE") -> {
                    Shell.cmd(SELINUX_PATCH_SCRIPT).exec()
                    RootState.Ready(DEFAULT_HID_PATH)
                }
                output.contains("HID_SETUP_ON_EXISTING_OK") -> {
                    Shell.cmd(SELINUX_PATCH_SCRIPT).exec()
                    RootState.Ready(DEFAULT_HID_PATH)
                }
                else -> {
                    RootState.Error("Setup sur gadget existant echoue: $output")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "trySetupOnExistingGadget error", e)
            RootState.Error("Erreur setup gadget existant: ${e.message}")
        }
    }

    /**
     * Desactive le gadget HID (cleanup).
     */
    suspend fun teardownHidGadget() = withContext(Dispatchers.IO) {
        try {
            Shell.cmd(
                """
                cd /sys/kernel/config/usb_gadget/g1 2>/dev/null || exit 0
                echo "" > UDC 2>/dev/null
                rm configs/c.1/hid.usb0 2>/dev/null
                rmdir configs/c.1/strings/0x409 2>/dev/null
                rmdir configs/c.1 2>/dev/null
                rmdir functions/hid.usb0 2>/dev/null
                rmdir strings/0x409 2>/dev/null
                cd .. && rmdir g1 2>/dev/null
                """.trimIndent()
            ).exec()
        } catch (_: Exception) {
            // Ignore les erreurs de cleanup
        }
    }
}
