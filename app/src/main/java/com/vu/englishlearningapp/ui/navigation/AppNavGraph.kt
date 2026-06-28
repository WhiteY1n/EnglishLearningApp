package com.vu.englishlearningapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.vu.englishlearningapp.di.AppContainer
import com.vu.englishlearningapp.ui.screens.auth.LoginScreen
import com.vu.englishlearningapp.ui.screens.auth.LoginViewModel
import com.vu.englishlearningapp.ui.screens.admin.dashboard.AdminDashboardScreen
import com.vu.englishlearningapp.ui.screens.admin.dashboard.DashboardViewModel
import com.vu.englishlearningapp.ui.screens.admin.users.UserManagementScreen
import com.vu.englishlearningapp.ui.screens.admin.collection.CollectionDetailScreen
import com.vu.englishlearningapp.ui.screens.admin.collection.CollectionDetailViewModel
import com.vu.englishlearningapp.ui.screens.admin.collection.CollectionListScreen
import com.vu.englishlearningapp.ui.screens.admin.collection.CollectionListViewModel
import com.vu.englishlearningapp.ui.screens.admin.collection.CreateCollectionScreen
import com.vu.englishlearningapp.ui.screens.admin.collection.CreateEditCollectionViewModel
import com.vu.englishlearningapp.ui.screens.admin.collection.EditCollectionScreen
import com.vu.englishlearningapp.ui.screens.flashcard.FlashcardCollectionListScreen
import com.vu.englishlearningapp.ui.screens.flashcard.FlashcardCollectionListViewModel
import com.vu.englishlearningapp.ui.screens.flashcard.FlashcardStudyScreen
import com.vu.englishlearningapp.ui.screens.flashcard.FlashcardStudyViewModel
import com.vu.englishlearningapp.ui.screens.home.HomeScreen
import com.vu.englishlearningapp.ui.screens.home.HomeViewModel
import com.vu.englishlearningapp.ui.screens.profile.EditProfileScreen
import com.vu.englishlearningapp.ui.screens.profile.EditProfileViewModel
import com.vu.englishlearningapp.ui.screens.profile.ProfileScreen
import com.vu.englishlearningapp.ui.screens.profile.ProfileViewModel
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
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onAdminDashboardClick = {
                    navController.navigate(Screen.AdminDashboard.route)
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

        // --- Profile Flow ---

        composable(Screen.Profile.route) {
            val vm: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(
                    appContainer.profileRepository,
                    appContainer.authRepository
                )
            )
            ProfileScreen(
                viewModel = vm,
                onEditClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.EditProfile.route) {
            val vm: EditProfileViewModel = viewModel(
                factory = EditProfileViewModel.Factory(appContainer.profileRepository)
            )

            // Pre-fill form with data from the previous ProfileScreen's back stack
            val profileEntry = navController.previousBackStackEntry
            val profileVm = profileEntry?.let {
                // Try to get the ProfileViewModel from the previous entry
                // If unavailable, the form will start empty and user can still type
                try {
                    ViewModelProvider(it, ProfileViewModel.Factory(
                        appContainer.profileRepository,
                        appContainer.authRepository
                    ))[ProfileViewModel::class.java]
                } catch (_: Exception) { null }
            }

            // Initialize form with existing profile data
            profileVm?.uiState?.value?.user?.let { user ->
                vm.initializeForm(
                    name = user.name,
                    phone = user.phone ?: "",
                    birthday = formatBirthdayForEdit(user.birthday),
                    address = user.address ?: "",
                    email = user.email
                )
            }

            EditProfileScreen(
                viewModel = vm,
                onSaveSuccess = {
                    // Go back to ProfileScreen (it will refresh)
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }

        // --- Admin Flow ---

        composable(Screen.AdminDashboard.route) {
            val vm: DashboardViewModel = viewModel(
                factory = DashboardViewModel.Factory()
            )
            // Pre-fill user state from HomeScreen / TokenManager logic if available
            // Alternatively, we get the current user from authRepository, but we can pass it down via HomeViewModel state earlier,
            // or we just fetch it from TokenManager. Here we can let DashboardViewModel be simple.
            // For now, since user info is in TokenManager or AuthRepo, let's inject it or leave it.
            // Actually, HomeViewModel has the user. We can retrieve it if we need.
            val homeEntry = navController.previousBackStackEntry
            val homeVm = homeEntry?.let {
                try {
                    ViewModelProvider(it, HomeViewModel.Factory(
                        appContainer.authRepository
                    ))[HomeViewModel::class.java]
                } catch (_: Exception) { null }
            }
            homeVm?.uiState?.value?.user?.let { user ->
                vm.setUser(user)
            }

            AdminDashboardScreen(
                viewModel = vm,
                onNavigateToRoute = { route ->
                    navController.navigate(route)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminUserManagement.route) {
            UserManagementScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminCollectionList.route) {
            val vm: CollectionListViewModel = viewModel(
                factory = CollectionListViewModel.Factory(appContainer.collectionRepository)
            )
            CollectionListScreen(
                viewModel = vm,
                onCollectionClick = { id ->
                    navController.navigate(Screen.AdminCollectionDetail.createRoute(id))
                },
                onCreateClick = {
                    navController.navigate(Screen.AdminCreateCollection.route)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AdminCollectionDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val vm: CollectionDetailViewModel = viewModel(
                factory = CollectionDetailViewModel.Factory(appContainer.collectionRepository, id)
            )
            CollectionDetailScreen(
                viewModel = vm,
                onEditClick = { collectionId ->
                    navController.navigate(Screen.AdminEditCollection.createRoute(collectionId))
                },
                onDeleteSuccess = {
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminCreateCollection.route) {
            val vm: CreateEditCollectionViewModel = viewModel(
                factory = CreateEditCollectionViewModel.Factory(appContainer.collectionRepository, null)
            )
            CreateCollectionScreen(
                viewModel = vm,
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.AdminEditCollection.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            val vm: CreateEditCollectionViewModel = viewModel(
                factory = CreateEditCollectionViewModel.Factory(appContainer.collectionRepository, id)
            )
            EditCollectionScreen(
                viewModel = vm,
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

/**
 * Extract date portion from ISO birthday string for the edit form.
 * e.g., "1989-12-31T17:00:00.000000Z" -> "1989-12-31"
 */
private fun formatBirthdayForEdit(birthday: String?): String {
    if (birthday.isNullOrEmpty()) return ""
    return if (birthday.contains("T")) {
        birthday.substringBefore("T")
    } else {
        birthday
    }
}
