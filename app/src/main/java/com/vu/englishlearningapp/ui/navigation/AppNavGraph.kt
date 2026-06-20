package com.vu.englishlearningapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vu.englishlearningapp.di.AppContainer
import com.vu.englishlearningapp.ui.screens.auth.LoginScreen
import com.vu.englishlearningapp.ui.screens.auth.LoginViewModel
import com.vu.englishlearningapp.ui.screens.flashcard.FlashcardCollectionListScreen
import com.vu.englishlearningapp.ui.screens.flashcard.FlashcardCollectionListViewModel
import com.vu.englishlearningapp.ui.screens.flashcard.FlashcardStudyScreen
import com.vu.englishlearningapp.ui.screens.flashcard.FlashcardStudyViewModel
import com.vu.englishlearningapp.ui.screens.home.HomeScreen
import com.vu.englishlearningapp.ui.screens.home.HomeViewModel
import com.vu.englishlearningapp.ui.screens.quiz.QuizListScreen
import com.vu.englishlearningapp.ui.screens.quiz.QuizListViewModel
import com.vu.englishlearningapp.ui.screens.quiz.QuizTakingScreen
import com.vu.englishlearningapp.ui.screens.quiz.QuizTakingViewModel
import com.vu.englishlearningapp.ui.screens.quiz.ResultScreen

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
        // --- Auth ---

        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.Factory(appContainer.authRepository)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // --- Home ---

        composable(Screen.Home.route) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(appContainer.authRepository)
            )
            HomeScreen(
                viewModel = homeViewModel,
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onFlashcardsClick = {
                    navController.navigate(Screen.FlashcardCollections.route)
                },
                onQuizzesClick = {
                    navController.navigate(Screen.QuizList.route)
                }
            )
        }

        // --- Flashcard Flow ---

        composable(Screen.FlashcardCollections.route) {
            val vm: FlashcardCollectionListViewModel = viewModel(
                factory = FlashcardCollectionListViewModel.Factory(appContainer.flashcardRepository)
            )
            FlashcardCollectionListScreen(
                viewModel = vm,
                onCollectionClick = { collectionId ->
                    navController.navigate(Screen.FlashcardStudy.createRoute(collectionId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.FlashcardStudy.route,
            arguments = listOf(navArgument("collectionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val collectionId = backStackEntry.arguments?.getInt("collectionId") ?: return@composable
            val vm: FlashcardStudyViewModel = viewModel(
                factory = FlashcardStudyViewModel.Factory(
                    appContainer.flashcardRepository,
                    collectionId
                )
            )
            FlashcardStudyScreen(
                viewModel = vm,
                onBackClick = { navController.popBackStack() }
            )
        }

        // --- Quiz Flow ---

        composable(Screen.QuizList.route) {
            val vm: QuizListViewModel = viewModel(
                factory = QuizListViewModel.Factory(appContainer.quizRepository)
            )
            QuizListScreen(
                viewModel = vm,
                onTestClick = { testId ->
                    navController.navigate(Screen.QuizTaking.createRoute(testId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.QuizTaking.route,
            arguments = listOf(navArgument("testId") { type = NavType.IntType })
        ) { backStackEntry ->
            val testId = backStackEntry.arguments?.getInt("testId") ?: return@composable
            val vm: QuizTakingViewModel = viewModel(
                factory = QuizTakingViewModel.Factory(appContainer.quizRepository, testId)
            )
            QuizTakingScreen(
                viewModel = vm,
                onQuizFinished = {
                    // Navigate to result and remove QuizTaking from back stack
                    navController.navigate(Screen.QuizResult.route) {
                        popUpTo(Screen.QuizList.route)
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.QuizResult.route) {
            ResultScreen(
                onBackToQuizzes = {
                    // Pop back to QuizList (removes ResultScreen from stack)
                    navController.popBackStack(Screen.QuizList.route, inclusive = false)
                }
            )
        }
    }
}
