package com.winrescue.data.root

import android.content.Context
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
        private const val DEFAULT_HID_PATH = "/dev/hidg0"

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
     * Appele quand l'etat est RootOnlyNoHid.
     */
    suspend fun setupHidGadget(): RootState = withContext(Dispatchers.IO) {
        try {
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
