package com.winrescue.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.winrescue.usb.HidKeyMap.KeyboardLayout
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "winrescue_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object Keys {
        val HID_DEVICE_PATH = stringPreferencesKey("hid_device_path")
        val KEYBOARD_LAYOUT = stringPreferencesKey("keyboard_layout")
        val CHAR_DELAY_MS = longPreferencesKey("char_delay_ms")
        val STEP_DELAY_MS = longPreferencesKey("step_delay_ms")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val PREVIEW_BEFORE_SEND = booleanPreferencesKey("preview_before_send")
        val DEBUG_MODE = booleanPreferencesKey("debug_mode")
        val DISCLAIMER_ACCEPTED = booleanPreferencesKey("disclaimer_accepted")
        val DISCLAIMER_TIMESTAMP = longPreferencesKey("disclaimer_timestamp")
    }

    override val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            hidDevicePath = prefs[Keys.HID_DEVICE_PATH] ?: "/dev/hidg0",
            keyboardLayout = prefs[Keys.KEYBOARD_LAYOUT]?.let {
                try {
                    KeyboardLayout.valueOf(it)
                } catch (_: Exception) {
                    KeyboardLayout.QWERTY_US
                }
            } ?: KeyboardLayout.QWERTY_US,
            charDelayMs = prefs[Keys.CHAR_DELAY_MS] ?: 50L,
            stepDelayMs = prefs[Keys.STEP_DELAY_MS] ?: 1000L,
            darkTheme = prefs[Keys.DARK_THEME] ?: true,
            previewBeforeSend = prefs[Keys.PREVIEW_BEFORE_SEND] ?: true,
            debugMode = prefs[Keys.DEBUG_MODE] ?: false,
            disclaimerAccepted = prefs[Keys.DISCLAIMER_ACCEPTED] ?: false,
            disclaimerTimestamp = prefs[Keys.DISCLAIMER_TIMESTAMP] ?: 0L
        )
    }

    override suspend fun updateKeyboardLayout(layout: KeyboardLayout) {
        context.dataStore.edit { it[Keys.KEYBOARD_LAYOUT] = layout.name }
    }

    override suspend fun updateCharDelay(delayMs: Long) {
        context.dataStore.edit { it[Keys.CHAR_DELAY_MS] = delayMs.coerceIn(10, 200) }
    }

    override suspend fun updateStepDelay(delayMs: Long) {
        context.dataStore.edit { it[Keys.STEP_DELAY_MS] = delayMs.coerceIn(500, 5000) }
    }

    override suspend fun updatePreviewBeforeSend(enabled: Boolean) {
        context.dataStore.edit { it[Keys.PREVIEW_BEFORE_SEND] = enabled }
    }

    override suspend fun updateDebugMode(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DEBUG_MODE] = enabled }
    }

    override suspend fun updateHidDevicePath(path: String) {
        context.dataStore.edit { it[Keys.HID_DEVICE_PATH] = path }
    }

    override suspend fun acceptDisclaimer() {
        context.dataStore.edit { prefs ->
            prefs[Keys.DISCLAIMER_ACCEPTED] = true
            prefs[Keys.DISCLAIMER_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    override suspend fun isDisclaimerAccepted(): Boolean {
        return context.dataStore.data.first()[Keys.DISCLAIMER_ACCEPTED] ?: false
    }
}
