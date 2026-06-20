package com.vu.englishlearningapp.ui.navigation

/**
 * Defines all navigation routes in the app.
 * Using a sealed class keeps routes type-safe and in one place.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
}
