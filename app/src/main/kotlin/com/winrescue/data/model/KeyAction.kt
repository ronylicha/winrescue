package com.winrescue.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class KeyAction {
    @Serializable
    @SerialName("string")
    data class TypeString(
        val value: String,
        val delayBetweenCharsMs: Long = 50
    ) : KeyAction()

    @Serializable
    @SerialName("key")
    data class PressKey(
        val key: String,
        val modifier: String? = null
    ) : KeyAction()

    @Serializable
    @SerialName("combination")
    data class KeyCombination(
        val keys: List<String>
    ) : KeyAction()

    @Serializable
    @SerialName("wait")
    data class Wait(
        val ms: Long
    ) : KeyAction()

    @Serializable
    @SerialName("repeat")
    data class RepeatKey(
        val key: String,
        val count: Int,
        val delayBetweenMs: Long = 100
    ) : KeyAction()

    @Serializable
    @SerialName("template")
    data class TemplateString(
        val template: String,
        val delayBetweenCharsMs: Long = 50
    ) : KeyAction()

    @Serializable
    @SerialName("shell")
    data class ShellCommand(
        val command: String,
        val root: Boolean = true,
        val description: String = ""
    ) : KeyAction()
}
