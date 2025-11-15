package com.example.myapp.navigation

sealed class Screen(val route: String) {
    object Tabs : Screen("tabs")
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Modal : Screen("modal")
    
    // Legacy screens (can be removed if not needed)
    object UserList : Screen("user_list")
    object Settings : Screen("settings")
}

