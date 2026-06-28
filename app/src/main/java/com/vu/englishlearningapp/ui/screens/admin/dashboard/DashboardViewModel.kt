package com.vu.englishlearningapp.ui.screens.admin.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.HelpCenter
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vu.englishlearningapp.data.remote.dto.auth.UserDto
import com.vu.englishlearningapp.ui.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardUiState(
    val user: UserDto? = null,
    val menuItems: List<AdminMenuItem> = emptyList()
)

class DashboardViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        // Initialize the reusable menu items
        val items = listOf(
            AdminMenuItem(
                title = "Flashcard Collections",
                icon = Icons.Default.Style,
                route = Screen.AdminCollectionList.route
            ),
            AdminMenuItem(
                title = "Flashcards",
                icon = Icons.Default.ViewCarousel,
                route = null,
                isComingSoon = true
            ),
            AdminMenuItem(
                title = "Questions",
                icon = Icons.Default.HelpCenter,
                route = null,
                isComingSoon = true
            ),
            AdminMenuItem(
                title = "Tests",
                icon = Icons.Default.Quiz,
                route = null,
                isComingSoon = true
            ),
            AdminMenuItem(
                title = "Statistics",
                icon = Icons.Default.Assessment,
                route = null,
                isComingSoon = true
            ),
            AdminMenuItem(
                title = "Users",
                icon = Icons.Default.People,
                route = Screen.AdminUserManagement.route
            )
        )
        _uiState.value = _uiState.value.copy(menuItems = items)
    }

    /**
     * Set the current user to display their name in the welcome text.
     */
    fun setUser(user: UserDto) {
        _uiState.value = _uiState.value.copy(user = user)
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                return DashboardViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
