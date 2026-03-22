package com.winrescue.data.repository

import com.winrescue.data.model.OsTarget
import com.winrescue.data.model.Script

interface ScriptRepository {
    suspend fun getAllScripts(): List<Script>
    suspend fun getScript(id: String): Script?
    suspend fun getScriptsByOs(os: OsTarget): List<Script>
}
