package com.example.myapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapp.ui.screens.ModalScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Tabs.route,
        modifier = modifier
    ) {
        composable(Screen.Tabs.route) {
            TabNavigation()
        }
        composable(Screen.Modal.route) {
            ModalScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

