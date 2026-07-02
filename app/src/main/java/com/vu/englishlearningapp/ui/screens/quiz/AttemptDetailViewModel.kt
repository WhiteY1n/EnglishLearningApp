package com.vu.englishlearningapp.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vu.englishlearningapp.data.remote.dto.quiz.AttemptDetailDto
import com.vu.englishlearningapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AttemptDetailUiState(
    val detail: AttemptDetailDto? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AttemptDetailViewModel(
    private val quizRepository: QuizRepository,
    private val attemptId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttemptDetailUiState())
    val uiState: StateFlow<AttemptDetailUiState> = _uiState.asStateFlow()

    init {
        loadAttempt()
    }

    fun loadAttempt() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                _uiState.value = AttemptDetailUiState(
                    detail = quizRepository.getAttempt(attemptId)
                )
            } catch (exception: Exception) {
                _uiState.value = AttemptDetailUiState(
                    errorMessage = exception.message ?: "Failed to load attempt details"
                )
            }
        }
    }

    class Factory(
        private val quizRepository: QuizRepository,
        private val attemptId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AttemptDetailViewModel::class.java)) {
                return AttemptDetailViewModel(quizRepository, attemptId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
