package com.winrescue.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ScriptStep(
    val id: Int,
    val title: String,
    val instruction: String,
    val instructionDetail: String? = null,
    val imageHint: String? = null,
    val confirmQuestion: String? = null,
    val waitBeforeSendMs: Long = 0,
    val waitAfterSendMs: Long = 0,
    val actions: List<KeyAction> = emptyList(),
    val retryable: Boolean = false,
    val retryInstruction: String? = null,
    val criticalStep: Boolean = false
)
