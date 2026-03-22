package com.winrescue.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winrescue.data.settings.AppSettings
import com.winrescue.data.settings.SettingsRepository
import com.winrescue.usb.HidKeyMap.KeyboardLayout
import com.winrescue.usb.HidKeyboardManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HidTestResult(
    val success: Boolean,
    val message: String
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val hidKeyboardManager: HidKeyboardManager
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppSettings()
        )

    private val _hidTestResult = MutableStateFlow<HidTestResult?>(null)
    val hidTestResult: StateFlow<HidTestResult?> = _hidTestResult.asStateFlow()

    private val _isTestingHid = MutableStateFlow(false)
    val isTestingHid: StateFlow<Boolean> = _isTestingHid.asStateFlow()

    fun updateKeyboardLayout(layout: KeyboardLayout) {
        viewModelScope.launch {
            settingsRepository.updateKeyboardLayout(layout)
        }
    }

    fun updateCharDelay(delayMs: Long) {
        viewModelScope.launch {
            settingsRepository.updateCharDelay(delayMs)
        }
    }

    fun updateStepDelay(delayMs: Long) {
        viewModelScope.launch {
            settingsRepository.updateStepDelay(delayMs)
        }
    }

    fun updatePreviewBeforeSend(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updatePreviewBeforeSend(enabled)
        }
    }

    fun updateDebugMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateDebugMode(enabled)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.updateLanguage(language)
        }
    }

    fun testHidConnection() {
        viewModelScope.launch {
            _isTestingHid.value = true
            _hidTestResult.value = null

            val available = hidKeyboardManager.isHidAvailable()
            if (!available) {
                _hidTestResult.value = HidTestResult(
                    success = false,
                    message = "Peripherique HID non disponible"
                )
                _isTestingHid.value = false
                return@launch
            }

            val connected = hidKeyboardManager.connect()
            if (connected) {
                hidKeyboardManager.disconnect()
                _hidTestResult.value = HidTestResult(
                    success = true,
                    message = "Connexion reussie"
                )
            } else {
                _hidTestResult.value = HidTestResult(
                    success = false,
                    message = "Echec de connexion au peripherique"
                )
            }

            _isTestingHid.value = false
        }
    }
}
