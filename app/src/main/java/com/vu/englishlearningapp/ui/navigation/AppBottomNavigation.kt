package com.vu.englishlearningapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

private data class BottomNavigationItem(
    val label: String,
    val route: String,
    val icon: ImageVector,
    val relatedRoutes: Set<String>
)

private val bottomNavigationItems = listOf(
    BottomNavigationItem(
        label = "Home",
        route = Screen.Home.route,
        icon = Icons.Default.Home,
        relatedRoutes = setOf(Screen.Home.route)
    ),
    BottomNavigationItem(
        label = "Flashcards",
        route = Screen.FlashcardCollections.route,
        icon = Icons.Default.Style,
        relatedRoutes = setOf(
            Screen.FlashcardCollections.route,
            Screen.FlashcardStudy.route
        )
    ),
    BottomNavigationItem(
        label = "Quizzes",
        route = Screen.QuizList.route,
        icon = Icons.Default.Quiz,
        relatedRoutes = setOf(
            Screen.QuizList.route,
            Screen.QuizDetail.route,
            Screen.QuizTaking.route,
            Screen.QuizResult.route
        )
    ),
    BottomNavigationItem(
        label = "History",
        route = Screen.AttemptHistory.route,
        icon = Icons.Default.History,
        relatedRoutes = setOf(
            Screen.AttemptHistory.route,
            Screen.AttemptHistoryDetail.route
        )
    ),
    BottomNavigationItem(
        label = "Profile",
        route = Screen.Profile.route,
        icon = Icons.Default.Person,
        relatedRoutes = setOf(
            Screen.Profile.route,
            Screen.EditProfile.route
        )
    )
)

@Composable
fun AppBottomNavigation(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 12.dp
    ) {
        bottomNavigationItems.forEach { item ->
            val isSelected = currentRoute in item.relatedRoutes
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF17191C),
                    indicatorColor = Color(0xFFDDF3FF),
                    unselectedIconColor = Color(0xFF71767F)
                )
            )
        }
    }
}
