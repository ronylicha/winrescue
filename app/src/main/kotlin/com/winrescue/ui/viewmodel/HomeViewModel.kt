package com.winrescue.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winrescue.data.model.OsTarget
import com.winrescue.data.model.Script
import com.winrescue.data.repository.ScriptRepository
import com.winrescue.data.root.RootManager
import com.winrescue.data.root.RootState
import com.winrescue.data.settings.SettingsRepository
import com.winrescue.ui.screens.HomeUiState
import com.winrescue.usb.UsbConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val scriptRepository: ScriptRepository,
    private val rootManager: RootManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _inputValues = MutableStateFlow<Map<String, String>>(emptyMap())
    val inputValues: StateFlow<Map<String, String>> = _inputValues.asStateFlow()

    private val _showDisclaimer = MutableStateFlow(false)
    val showDisclaimer: StateFlow<Boolean> = _showDisclaimer.asStateFlow()

    private var allScripts: List<Script> = emptyList()

    init {
        checkDisclaimer()
        loadScripts()
        checkRoot()
    }

    private fun checkDisclaimer() {
        viewModelScope.launch {
            val accepted = settingsRepository.isDisclaimerAccepted()
            _showDisclaimer.value = !accepted
        }
    }

    fun acceptDisclaimer() {
        viewModelScope.launch {
            settingsRepository.acceptDisclaimer()
            _showDisclaimer.value = false
        }
    }

    private fun loadScripts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                allScripts = scriptRepository.getAllScripts()
                filterScripts()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkRoot() {
        viewModelScope.launch {
            _uiState.update { it.copy(rootState = RootState.Checking) }
            val rootState = rootManager.checkRootStatus()
            _uiState.update { it.copy(rootState = rootState) }
        }
    }

    fun selectOs(os: OsTarget) {
        _uiState.update { it.copy(selectedOs = os) }
        filterScripts()
    }

    private fun filterScripts() {
        val selectedOs = _uiState.value.selectedOs
        val filtered = allScripts.filter { script ->
            script.os.contains(selectedOs) || script.os.contains(OsTarget.BOTH)
        }
        _uiState.update { it.copy(scripts = filtered) }
    }

    fun getScriptById(id: String): Script? {
        return allScripts.find { it.id == id }
    }

    fun updateInput(fieldId: String, value: String) {
        _inputValues.update { current ->
            current.toMutableMap().apply { put(fieldId, value) }
        }
    }

    fun clearInputs() {
        _inputValues.update { emptyMap() }
    }

    fun updateUsbState(state: UsbConnectionState) {
        _uiState.update { it.copy(usbState = state) }
    }
}
