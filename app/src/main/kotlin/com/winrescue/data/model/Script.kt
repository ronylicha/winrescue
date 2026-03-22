package com.winrescue.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Script(
    val id: String,
    val name: String,
    val description: String,
    val category: ScriptCategory,
    val os: List<OsTarget>,
    val difficulty: Difficulty,
    val estimatedMinutes: Int,
    val requiresRoot: Boolean = true,
    val icon: String,
    val warningMessage: String? = null,
    val inputFields: List<InputField> = emptyList(),
    val steps: List<ScriptStep>
)

@Serializable
data class InputField(
    val id: String,
    val label: String,
    val placeholder: String,
    val defaultValue: String? = null,
    val required: Boolean = true,
    val inputType: InputType = InputType.TEXT
)
