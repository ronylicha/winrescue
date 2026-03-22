package com.winrescue.ui.navigation

sealed class Route(val route: String) {
    data object Home : Route("home")
    data object Settings : Route("settings")

    data class ScriptDetail(val scriptId: String = "{scriptId}") : Route("script/{scriptId}") {
        companion object {
            fun createRoute(scriptId: String) = "script/$scriptId"
        }
    }

    data class Wizard(val scriptId: String = "{scriptId}", val stepId: Int = 0) : Route("wizard/{scriptId}/step/{stepId}") {
        companion object {
            fun createRoute(scriptId: String, stepId: Int) = "wizard/$scriptId/step/$stepId"
        }
    }

    data class Success(val scriptId: String = "{scriptId}") : Route("success/{scriptId}") {
        companion object {
            fun createRoute(scriptId: String) = "success/$scriptId"
        }
    }

    data class Error(val scriptId: String = "{scriptId}", val stepId: Int = 0) : Route("error/{scriptId}/step/{stepId}?errorMessage={errorMessage}") {
        companion object {
            fun createRoute(scriptId: String, stepId: Int, errorMessage: String? = null): String {
                val base = "error/$scriptId/step/$stepId"
                return if (errorMessage != null) {
                    "$base?errorMessage=${java.net.URLEncoder.encode(errorMessage, "UTF-8")}"
                } else {
                    base
                }
            }
        }
    }
}
