package com.vu.englishlearningapp.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vu.englishlearningapp.data.remote.dto.quiz.QuestionDto
import com.vu.englishlearningapp.data.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the quiz-taking screen.
 */
data class QuizTakingUiState(
    val testName: String = "",
    val questions: List<QuestionDto> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, String> = emptyMap(), // questionIndex -> selected answer
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isFinished: Boolean = false
) {
    /** The question currently being shown. */
    val currentQuestion: QuestionDto?
        get() = questions.getOrNull(currentIndex)

    /** The answer selected for the current question (if any). */
    val currentSelectedAnswer: String?
        get() = selectedAnswers[currentIndex]

    /** Progress text like "1 / 3". */
    val progress: String
        get() = if (questions.isEmpty()) "0 / 0"
        else "${currentIndex + 1} / ${questions.size}"

    /** Whether this is the last question. */
    val isLastQuestion: Boolean
        get() = currentIndex == questions.size - 1
}

/**
 * ViewModel for taking a quiz.
 * Handles question navigation, answer selection, and result computation.
 */
class QuizTakingViewModel(
    private val quizRepository: QuizRepository,
    private val testId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizTakingUiState())
    val uiState: StateFlow<QuizTakingUiState> = _uiState.asStateFlow()

    init {
        loadTest()
    }

    /**
     * Load the test detail (with questions) from the API.
     */
    private fun loadTest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val detail = quizRepository.getTestDetail(testId)
                _uiState.value = _uiState.value.copy(
                    testName = detail.testName,
                    questions = detail.questions,
                    isLoading = false,
                    currentIndex = 0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load quiz"
                )
            }
        }
    }

    /** Select an answer for the current question. */
    fun selectAnswer(answer: String) {
        val current = _uiState.value
        _uiState.value = current.copy(
            selectedAnswers = current.selectedAnswers + (current.currentIndex to answer)
        )
    }

    /** Move to the next question. */
    fun nextQuestion() {
        val current = _uiState.value
        if (current.currentIndex < current.questions.size - 1) {
            _uiState.value = current.copy(currentIndex = current.currentIndex + 1)
        }
    }

    /**
     * Finish the quiz: compare answers, compute score, and store result.
     */
    fun finishQuiz() {
        val current = _uiState.value

        // Build the review list by comparing each answer
        val reviewItems = current.questions.mapIndexed { index, question ->
            val userAnswer = current.selectedAnswers[index] ?: "(no answer)"
            val correctAnswer = question.getCorrectAnswer()
            ReviewItem(
                questionText = question.questionText,
                userAnswer = userAnswer,
                correctAnswer = correctAnswer,
                isCorrect = userAnswer == correctAnswer
            )
        }

        val score = reviewItems.count { it.isCorrect }

        // Store result for ResultScreen to read
        QuizResultHolder.result = QuizResult(
            testName = current.testName,
            score = score,
            total = current.questions.size,
            reviewItems = reviewItems
        )

        _uiState.value = current.copy(isFinished = true)

        // TODO: In the future, submit attempt results to backend via POST API
        // TODO: Example: quizRepository.submitAttempt(testId, selectedAnswers)
    }

    /**
     * Factory for creating this ViewModel with dependencies.
     */
    class Factory(
        private val quizRepository: QuizRepository,
        private val testId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizTakingViewModel::class.java)) {
                return QuizTakingViewModel(quizRepository, testId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
