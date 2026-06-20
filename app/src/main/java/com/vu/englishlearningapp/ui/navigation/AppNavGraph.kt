package com.vu.englishlearningapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vu.englishlearningapp.di.AppContainer
import com.vu.englishlearningapp.ui.screens.auth.LoginScreen
import com.vu.englishlearningapp.ui.screens.auth.LoginViewModel
import com.vu.englishlearningapp.ui.screens.home.HomeScreen
import com.vu.englishlearningapp.ui.screens.home.HomeViewModel

/**
 * Main navigation graph for the app.
 *
 * @param navController The NavHostController that manages navigation.
 * @param appContainer The DI container for creating ViewModels.
 * @param startDestination The initial screen to show (login or home).
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.Factory(appContainer.authRepository)
            )

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // Navigate to Home and clear the back stack so user can't go back to login
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(appContainer.authRepository)
            )

            HomeScreen(
                viewModel = homeViewModel,
                onLoggedOut = {
                    // Navigate back to Login and clear the back stack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
