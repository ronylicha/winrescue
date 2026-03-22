package com.winrescue.data.root

/**
 * Etat du disclaimer legal.
 * Stocke dans DataStore, verifie au premier lancement.
 */
data class DisclaimerState(
    val accepted: Boolean = false,
    val acceptedTimestamp: Long = 0L
)
