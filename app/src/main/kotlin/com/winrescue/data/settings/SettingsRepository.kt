package com.winrescue.data.settings

import com.winrescue.usb.HidKeyMap.KeyboardLayout
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun updateKeyboardLayout(layout: KeyboardLayout)
    suspend fun updateCharDelay(delayMs: Long)
    suspend fun updateStepDelay(delayMs: Long)
    suspend fun updatePreviewBeforeSend(enabled: Boolean)
    suspend fun updateDebugMode(enabled: Boolean)
    suspend fun updateHidDevicePath(path: String)
    suspend fun acceptDisclaimer()
    suspend fun isDisclaimerAccepted(): Boolean
}
