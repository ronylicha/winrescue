package com.winrescue.data.settings

import com.winrescue.usb.HidKeyMap.KeyboardLayout

data class AppSettings(
    val hidDevicePath: String = "/dev/hidg0",
    val keyboardLayout: KeyboardLayout = KeyboardLayout.QWERTY_US,
    val charDelayMs: Long = 50L,
    val stepDelayMs: Long = 1000L,
    val darkTheme: Boolean = true,
    val previewBeforeSend: Boolean = true,
    val debugMode: Boolean = false,
    val disclaimerAccepted: Boolean = false,
    val disclaimerTimestamp: Long = 0L
)
