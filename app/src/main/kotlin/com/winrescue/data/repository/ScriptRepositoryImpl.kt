package com.winrescue.data.repository

import android.content.Context
import com.winrescue.data.model.OsTarget
import com.winrescue.data.model.Script
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScriptRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ScriptRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    private var cachedScripts: List<Script>? = null
    private val mutex = Mutex()

    override suspend fun getAllScripts(): List<Script> = mutex.withLock {
        cachedScripts ?: loadScripts().also { cachedScripts = it }
    }

    override suspend fun getScript(id: String): Script? {
        return getAllScripts().find { it.id == id }
    }

    override suspend fun getScriptsByOs(os: OsTarget): List<Script> {
        return getAllScripts().filter { script ->
            script.os.contains(os) || script.os.contains(OsTarget.BOTH)
        }
    }

    private suspend fun loadScripts(): List<Script> = withContext(Dispatchers.IO) {
        val scripts = mutableListOf<Script>()
        val assetManager = context.assets

        try {
            val scriptFiles = assetManager.list("scripts") ?: emptyArray()
            for (fileName in scriptFiles) {
                if (!fileName.endsWith(".json")) continue
                try {
                    val jsonString = assetManager.open("scripts/$fileName")
                        .bufferedReader()
                        .use { it.readText() }
                    val script = json.decodeFromString<Script>(jsonString)
                    scripts.add(script)
                } catch (e: Exception) {
                    android.util.Log.e("ScriptRepository", "Failed to load $fileName", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("ScriptRepository", "Failed to list scripts", e)
        }

        scripts.sortedBy { it.name }
    }
}
