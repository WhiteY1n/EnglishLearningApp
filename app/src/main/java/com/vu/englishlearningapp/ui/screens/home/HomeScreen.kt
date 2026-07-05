package com.vu.englishlearningapp.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vu.englishlearningapp.core.permission.PermissionHelper

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onLoggedOut: () -> Unit,
    onFlashcardsClick: () -> Unit = {},
    onQuizzesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAdminDashboardClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(HomeCategory.ALL) }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) onLoggedOut()
    }

    val permissionHelper = remember(uiState.user) {
        PermissionHelper(uiState.user)
    }
    val canViewFlashcards = permissionHelper.checkPermission("flashcard.view")
    val canViewQuizzes = permissionHelper.checkPermission("quizzies.view")
    val canAccessAdminDashboard = listOf(
        "flashcard_collection.view",
        "flashcard.view",
        "question.view",
        "collection_test.view",
        "user_test_attempt.view",
        "user.view",
        "permission.view",
        "role.view"
    ).any(permissionHelper::checkPermission)

    LaunchedEffect(canViewFlashcards, canViewQuizzes, canAccessAdminDashboard) {
        val selectedCategoryAvailable = when (selectedCategory) {
            HomeCategory.LEARN -> canViewFlashcards
            HomeCategory.TEST -> canViewQuizzes
            HomeCategory.ADMIN -> canAccessAdminDashboard
            else -> true
        }
        if (!selectedCategoryAvailable) selectedCategory = HomeCategory.ALL
    }

    val modules = buildList {
        if (canViewFlashcards) add(
            HomeModule(
                title = "Flashcards",
                description = "Build vocabulary with focused card collections",
                category = HomeCategory.LEARN,
                icon = Icons.Default.Style,
                onClick = onFlashcardsClick
            )
        )
        if (canViewQuizzes) add(
            HomeModule(
                title = "Quizzes",
                description = "Check your progress with interactive tests",
                category = HomeCategory.TEST,
                icon = Icons.Default.Quiz,
                onClick = onQuizzesClick
            )
        )
        add(
            HomeModule(
                title = "Profile",
                description = "Review and update your personal information",
                category = HomeCategory.ACCOUNT,
                icon = Icons.Default.Person,
                onClick = onProfileClick
            )
        )
        if (canAccessAdminDashboard) {
            add(
                HomeModule(
                    title = "Admin Dashboard",
                    description = "Manage learning content and system data",
                    category = HomeCategory.ADMIN,
                    icon = Icons.Default.AdminPanelSettings,
                    onClick = onAdminDashboardClick
                )
            )
        }
    }

    val visibleModules = modules.filter { module ->
        val matchesCategory = selectedCategory == HomeCategory.ALL ||
            module.category == selectedCategory
        val matchesSearch = searchQuery.isBlank() ||
            module.title.contains(searchQuery, ignoreCase = true) ||
            module.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        containerColor = HomeColors.Background,
        contentWindowInsets = WindowInsets.statusBars
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.user == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.user == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    androidx.compose.material3.Button(onClick = viewModel::loadCurrentUser) {
                        Text("Try again")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    item {
                        HomeHeader(
                            userName = uiState.user?.name.orEmpty(),
                            onProfileClick = onProfileClick,
                            onLogoutClick = viewModel::logout
                        )
                    }

                    item {
                        HomeSearchField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it }
                        )
                    }

                    uiState.errorMessage?.let { errorMessage ->
                        item { HomeErrorMessage(message = errorMessage) }
                    }

                    if (canViewFlashcards || canViewQuizzes) item {
                        HomeRecommendations(
                            showFlashcards = canViewFlashcards,
                            showQuizzes = canViewQuizzes,
                            onFlashcardsClick = onFlashcardsClick,
                            onQuizzesClick = onQuizzesClick
                        )
                    }

                    item {
                        Text(
                            text = "Explore modules",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        HomeCategorySelector(
                            selectedCategory = selectedCategory,
                            showLearnCategory = canViewFlashcards,
                            showTestCategory = canViewQuizzes,
                            showAdminCategory = canAccessAdminDashboard,
                            onCategorySelected = { selectedCategory = it }
                        )
                    }

                    if (visibleModules.isEmpty()) {
                        item { EmptyModuleResult() }
                    } else {
                        items(
                            items = visibleModules,
                            key = { it.title }
                        ) { module ->
                            HomeModuleCard(module = module)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(4.dp)) }
                }
            }
        }
    }
}
