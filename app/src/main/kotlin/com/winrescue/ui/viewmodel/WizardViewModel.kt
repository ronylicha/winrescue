package com.winrescue.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winrescue.data.model.KeyAction
import com.winrescue.data.model.Script
import com.winrescue.data.model.ScriptStep
import com.winrescue.data.repository.ScriptRepository
import com.winrescue.data.settings.SettingsRepository
import com.winrescue.usb.HidKeyboardManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StepState {
    data object WaitingConfirm : StepState()
    data object Sending : StepState()
    data object WaitingResult : StepState()
    data class Error(val message: String) : StepState()
    data object Success : StepState()
}

data class WizardState(
    val script: Script? = null,
    val currentStepIndex: Int = 0,
    val userInputs: Map<String, String> = emptyMap(),
    val stepState: StepState = StepState.WaitingConfirm,
    val sendingProgress: Float = 0f,
    val sendingDetail: String = "",
    val errorMessage: String? = null,
    val retryCount: Int = 0
) {
    val currentStep: ScriptStep?
        get() = script?.steps?.getOrNull(currentStepIndex)

    val totalSteps: Int
        get() = script?.steps?.size ?: 0

    val isLastStep: Boolean
        get() = currentStepIndex >= totalSteps - 1
}

@HiltViewModel
class WizardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val scriptRepository: ScriptRepository,
    private val hidKeyboardManager: HidKeyboardManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WizardState())
    val state: StateFlow<WizardState> = _state.asStateFlow()

    private var sendingJob: Job? = null

    private val scriptId: String = savedStateHandle["scriptId"] ?: ""
    private val initialStepId: Int = savedStateHandle["stepId"] ?: 0

    init {
        loadScript(scriptId)
    }

    private fun loadScript(scriptId: String) {
        viewModelScope.launch {
            val script = scriptRepository.getScript(scriptId)
            if (script != null) {
                val stepIndex = (initialStepId - 1).coerceIn(0, script.steps.size - 1)
                _state.update {
                    it.copy(
                        script = script,
                        currentStepIndex = stepIndex,
                        stepState = StepState.WaitingConfirm
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        stepState = StepState.Error("Script introuvable : $scriptId")
                    )
                }
            }
        }
    }

    fun setUserInputs(inputs: Map<String, String>) {
        _state.update { it.copy(userInputs = inputs) }
    }

    fun confirmAndSend() {
        val currentState = _state.value
        val step = currentState.currentStep ?: return

        sendingJob?.cancel()
        sendingJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    stepState = StepState.Sending,
                    sendingProgress = 0f,
                    sendingDetail = ""
                )
            }

            try {
                if (step.waitBeforeSendMs > 0) {
                    _state.update { it.copy(sendingDetail = "Attente avant envoi...") }
                    delay(step.waitBeforeSendMs)
                }

                val settings = settingsRepository.settings.first()
                val totalActions = step.actions.size
                val connected = hidKeyboardManager.connect()
                if (!connected) {
                    _state.update {
                        it.copy(
                            stepState = StepState.Error("Impossible de se connecter au peripherique HID."),
                            errorMessage = "Impossible de se connecter au peripherique HID."
                        )
                    }
                    return@launch
                }

                for ((index, action) in step.actions.withIndex()) {
                    val actionDescription = describeAction(action, currentState.userInputs)
                    _state.update {
                        it.copy(
                            sendingProgress = (index.toFloat()) / totalActions.coerceAtLeast(1),
                            sendingDetail = actionDescription
                        )
                    }

                    hidKeyboardManager.sendKeyAction(
                        action = action,
                        inputs = currentState.userInputs,
                        layout = settings.keyboardLayout
                    )
                }

                _state.update { it.copy(sendingProgress = 1f, sendingDetail = "Envoi termine") }

                if (step.waitAfterSendMs > 0) {
                    _state.update { it.copy(sendingDetail = "Attente apres envoi...") }
                    delay(step.waitAfterSendMs)
                }

                hidKeyboardManager.disconnect()

                if (step.confirmQuestion != null) {
                    _state.update { it.copy(stepState = StepState.WaitingResult) }
                } else {
                    nextStep()
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                hidKeyboardManager.disconnect()
                _state.update {
                    it.copy(
                        stepState = StepState.WaitingConfirm,
                        sendingProgress = 0f,
                        sendingDetail = ""
                    )
                }
            } catch (e: Exception) {
                hidKeyboardManager.disconnect()
                val errorMsg = e.message ?: "Erreur inconnue lors de l'envoi"
                _state.update {
                    it.copy(
                        stepState = StepState.Error(errorMsg),
                        errorMessage = errorMsg
                    )
                }
            }
        }
    }

    fun confirmSuccess() {
        nextStep()
    }

    fun retry() {
        _state.update {
            it.copy(
                stepState = StepState.WaitingConfirm,
                retryCount = it.retryCount + 1,
                sendingProgress = 0f,
                sendingDetail = "",
                errorMessage = null
            )
        }
    }

    fun cancelSending() {
        sendingJob?.cancel()
        sendingJob = null
    }

    fun previousStep() {
        val currentState = _state.value
        if (currentState.currentStepIndex > 0) {
            _state.update {
                it.copy(
                    currentStepIndex = it.currentStepIndex - 1,
                    stepState = StepState.WaitingConfirm,
                    sendingProgress = 0f,
                    sendingDetail = "",
                    retryCount = 0,
                    errorMessage = null
                )
            }
        }
    }

    private fun nextStep() {
        val currentState = _state.value
        if (currentState.isLastStep) {
            _state.update { it.copy(stepState = StepState.Success) }
        } else {
            _state.update {
                it.copy(
                    currentStepIndex = it.currentStepIndex + 1,
                    stepState = StepState.WaitingConfirm,
                    sendingProgress = 0f,
                    sendingDetail = "",
                    retryCount = 0,
                    errorMessage = null
                )
            }
        }
    }

    private fun describeAction(action: KeyAction, inputs: Map<String, String>): String {
        return when (action) {
            is KeyAction.TypeString -> "Saisie : ${action.value}"
            is KeyAction.PressKey -> {
                if (action.modifier != null) {
                    "Touche : ${action.modifier}+${action.key}"
                } else {
                    "Touche : ${action.key}"
                }
            }
            is KeyAction.KeyCombination -> "Combinaison : ${action.keys.joinToString("+")}"
            is KeyAction.Wait -> "Attente ${action.ms}ms"
            is KeyAction.RepeatKey -> "Repetition : ${action.key} x ${action.count}"
            is KeyAction.TemplateString -> {
                var resolved = action.template
                inputs.forEach { (key, value) ->
                    resolved = resolved.replace("{{$key}}", value)
                }
                "Saisie : $resolved"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sendingJob?.cancel()
        hidKeyboardManager.disconnect()
    }
}
