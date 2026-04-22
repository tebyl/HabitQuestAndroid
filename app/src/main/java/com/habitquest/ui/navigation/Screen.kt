package com.habitquest.ui.navigation

sealed class Screen(val route: String) {
    data object Home     : Screen("home")
    data object Progress : Screen("progress")
    data object Profile  : Screen("profile")
}
