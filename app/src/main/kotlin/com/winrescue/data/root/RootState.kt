package com.winrescue.data.root

sealed class RootState {
    /** Verification pas encore lancee */
    data object Unchecked : RootState()

    /** Verification en cours */
    data object Checking : RootState()

    /** Root OK + /dev/hidg0 accessible en ecriture */
    data class Ready(val hidDevicePath: String = "/dev/hidg0") : RootState()

    /** Root OK mais /dev/hidg0 n'existe pas — proposer setup configfs */
    data object RootOnlyNoHid : RootState()

    /** Pas de root detecte (su absent) */
    data object NoRoot : RootState()

    /** Root refuse par l'utilisateur (dialog Magisk refuse) */
    data object RootDenied : RootState()

    /** Erreur pendant la verification */
    data class Error(val message: String) : RootState()

    /** Indique si les fonctions HID sont utilisables */
    val isHidReady: Boolean get() = this is Ready

    /** Indique si le root est disponible (meme sans HID) */
    val hasRoot: Boolean get() = this is Ready || this is RootOnlyNoHid
}
