package com.example.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.screens.main.MainScreen
import com.example.presentation.screens.task.TaskScreen
import com.example.presentation.screens.task.TaskScreenViewModel
import com.example.presentation.ui.theme.AppTheme

sealed class NavGraph(val route: String) {
    object MainScreen : NavGraph(route = "main")

    object TaskScreen : NavGraph(route = "tasks/{${TaskScreenViewModel.TASK_ID_KEY}}") {
        fun passTaskId(id: Long) = "tasks/$id"
        const val newTask = "tasks/${TaskScreenViewModel.NEW_TASK}"
    }

    companion object {
        const val ROUTE = "graph"
    }
}


@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        route = NavGraph.ROUTE,
        startDestination = NavGraph.MainScreen.route,
        modifier = Modifier
            .background(AppTheme.colors.primaryBackground)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        composable(route = NavGraph.MainScreen.route) {
            MainScreen(navController)
        }

        composable(
            route = NavGraph.TaskScreen.route,
            arguments = listOf(
                navArgument(TaskScreenViewModel.TASK_ID_KEY) {
                    type = NavType.StringType
                    defaultValue = TaskScreenViewModel.NEW_TASK
                }
            )
        ) {
            TaskScreen(navController)
        }
    }
}