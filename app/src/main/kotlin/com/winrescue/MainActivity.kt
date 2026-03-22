package com.winrescue

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.winrescue.data.settings.SettingsRepository
import com.winrescue.ui.navigation.WinRescueNavGraph
import com.winrescue.ui.theme.WinRescueTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun attachBaseContext(newBase: Context) {
        val localizedContext = try {
            val settings = runBlocking { settingsRepository.settings.first() }
            val locale = when (settings.language) {
                "fr" -> Locale("fr")
                "en" -> Locale("en")
                else -> Locale.getDefault()
            }
            val config = newBase.resources.configuration
            config.setLocale(locale)
            newBase.createConfigurationContext(config)
        } catch (_: Exception) {
            newBase
        }
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WinRescueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    WinRescueNavGraph(navController = navController)
                }
            }
        }
    }
}
