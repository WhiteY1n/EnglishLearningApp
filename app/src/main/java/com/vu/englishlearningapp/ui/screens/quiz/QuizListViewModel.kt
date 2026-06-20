package com.vu.englishlearningapp.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vu.englishlearningapp.data.remote.dto.quiz.CollectionTestDto
import com.vu.englishlearningapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the quiz list screen.
 */
data class QuizListUiState(
    val tests: List<CollectionTestDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for displaying the list of available quizzes/tests.
 */
class QuizListViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizListUiState())
    val uiState: StateFlow<QuizListUiState> = _uiState.asStateFlow()

    init {
        loadTests()
    }

    /**
     * Load all available tests from the API.
     */
    fun loadTests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val tests = quizRepository.getTests()
                _uiState.value = _uiState.value.copy(
                    tests = tests,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load quizzes"
                )
            }
        }
    }

    /**
     * Factory for creating this ViewModel with dependencies.
     */
    class Factory(
        private val quizRepository: QuizRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizListViewModel::class.java)) {
                return QuizListViewModel(quizRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
