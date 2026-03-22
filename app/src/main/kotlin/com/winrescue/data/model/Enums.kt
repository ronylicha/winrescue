package com.winrescue.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class OsTarget {
    WIN10, WIN11, BOTH
}

@Serializable
enum class ScriptCategory {
    RECOVERY, ADMIN, REPAIR, SECURITY, NETWORK, DIAGNOSTIC
}

@Serializable
enum class Difficulty {
    EASY, MEDIUM, ADVANCED
}

@Serializable
enum class InputType {
    TEXT, PASSWORD, LETTER, NUMBER
}
