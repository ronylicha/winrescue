package com.winrescue.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.winrescue.ui.screens.ErrorScreen
import com.winrescue.ui.screens.HomeScreen
import com.winrescue.ui.screens.ScriptDetailScreen
import com.winrescue.ui.screens.SettingsScreen
import com.winrescue.ui.screens.SuccessScreen
import com.winrescue.ui.screens.WizardStepScreen

@Composable
fun WinRescueNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Route.Home.route
    ) {
        composable(Route.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(
            route = Route.ScriptDetail().route,
            arguments = listOf(
                navArgument("scriptId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val scriptId = backStackEntry.arguments?.getString("scriptId") ?: ""
            ScriptDetailScreen(
                scriptId = scriptId,
                navController = navController
            )
        }

        composable(
            route = Route.Wizard().route,
            arguments = listOf(
                navArgument("scriptId") { type = NavType.StringType },
                navArgument("stepId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val scriptId = backStackEntry.arguments?.getString("scriptId") ?: ""
            val stepId = backStackEntry.arguments?.getInt("stepId") ?: 0
            WizardStepScreen(
                scriptId = scriptId,
                stepId = stepId,
                navController = navController
            )
        }

        composable(
            route = Route.Success().route,
            arguments = listOf(
                navArgument("scriptId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val scriptId = backStackEntry.arguments?.getString("scriptId") ?: ""
            SuccessScreen(
                scriptId = scriptId,
                navController = navController
            )
        }

        composable(
            route = Route.Error().route,
            arguments = listOf(
                navArgument("scriptId") { type = NavType.StringType },
                navArgument("stepId") { type = NavType.IntType },
                navArgument("errorMessage") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val scriptId = backStackEntry.arguments?.getString("scriptId") ?: ""
            val stepId = backStackEntry.arguments?.getInt("stepId") ?: 0
            val errorMessage = backStackEntry.arguments?.getString("errorMessage")
            ErrorScreen(
                scriptId = scriptId,
                stepId = stepId,
                errorMessage = errorMessage,
                navController = navController
            )
        }

        composable(Route.Settings.route) {
            SettingsScreen(
                navController = navController
            )
        }
    }
}
