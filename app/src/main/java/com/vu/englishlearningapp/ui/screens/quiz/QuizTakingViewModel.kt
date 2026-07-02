package com.vu.englishlearningapp.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vu.englishlearningapp.data.remote.dto.quiz.AttemptDetailDto
import com.vu.englishlearningapp.data.remote.dto.quiz.QuestionDto
import com.vu.englishlearningapp.data.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizTakingUiState(
    val attemptId: Int? = null,
    val testName: String = "",
    val questions: List<QuestionDto> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswers: Map<Int, String> = emptyMap(),
    val remainingSeconds: Int = 0,
    val isLoading: Boolean = false,
    val isSavingAnswer: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val answerErrorMessage: String? = null,
    val isFinished: Boolean = false
) {
    val currentQuestion: QuestionDto?
        get() = questions.getOrNull(currentIndex)

    val currentSelectedAnswer: String?
        get() = currentQuestion?.let { selectedAnswers[it.id] }

    val progress: String
        get() = if (questions.isEmpty()) "0 / 0" else "${currentIndex + 1} / ${questions.size}"

    val isLastQuestion: Boolean
        get() = currentIndex == questions.size - 1

    val formattedRemainingTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}

class QuizTakingViewModel(
    private val quizRepository: QuizRepository,
    private val testId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizTakingUiState())
    val uiState: StateFlow<QuizTakingUiState> = _uiState.asStateFlow()
    private var timerJob: Job? = null

    init {
        startAttempt()
    }

    private fun startAttempt() {
        viewModelScope.launch {
            _uiState.value = QuizTakingUiState(isLoading = true)
            try {
                val startedAttempt = quizRepository.startAttempt(testId)
                val attemptDetail = quizRepository.getAttempt(startedAttempt.attemptId)
                val questions = quizRepository.getAttemptQuestions(startedAttempt.attemptId)
                val savedAnswers = questions.mapNotNull { question ->
                    question.userAnswer?.let { question.id to it }
                }.toMap()
                val firstUnansweredIndex = questions.indexOfFirst { it.userAnswer == null }

                _uiState.value = QuizTakingUiState(
                    attemptId = startedAttempt.attemptId,
                    testName = attemptDetail.attempt.collectionTest?.testName.orEmpty(),
                    questions = questions,
                    currentIndex = firstUnansweredIndex.takeIf { it >= 0 } ?: 0,
                    selectedAnswers = savedAnswers,
                    remainingSeconds = attemptDetail.remainingSeconds
                )
                startTimer()
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "Failed to start quiz"
                )
            }
        }
    }

    fun selectAnswer(answer: String) {
        val current = _uiState.value
        val attemptId = current.attemptId ?: return
        val question = current.currentQuestion ?: return
        if (current.isSavingAnswer || current.isSubmitting) return

        val previousAnswer = current.selectedAnswers[question.id]
        _uiState.value = current.copy(
            selectedAnswers = current.selectedAnswers + (question.id to answer),
            isSavingAnswer = true,
            answerErrorMessage = null
        )

        viewModelScope.launch {
            try {
                quizRepository.saveAnswer(attemptId, question.id, answer)
                _uiState.value = _uiState.value.copy(isSavingAnswer = false)
            } catch (exception: Exception) {
                val restoredAnswers = if (previousAnswer == null) {
                    _uiState.value.selectedAnswers - question.id
                } else {
                    _uiState.value.selectedAnswers + (question.id to previousAnswer)
                }
                _uiState.value = _uiState.value.copy(
                    selectedAnswers = restoredAnswers,
                    isSavingAnswer = false,
                    answerErrorMessage = exception.message ?: "Could not save answer"
                )
            }
        }
    }

    fun nextQuestion() {
        val current = _uiState.value
        if (!current.isSavingAnswer && current.currentIndex < current.questions.size - 1) {
            _uiState.value = current.copy(
                currentIndex = current.currentIndex + 1,
                answerErrorMessage = null
            )
        }
    }

    fun finishQuiz() {
        submitAttempt()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && !_uiState.value.isFinished) {
                delay(1_000)
                _uiState.value = _uiState.value.copy(
                    remainingSeconds = (_uiState.value.remainingSeconds - 1).coerceAtLeast(0)
                )
            }
            if (!_uiState.value.isFinished) {
                submitAttempt()
            }
        }
    }

    private fun submitAttempt() {
        val current = _uiState.value
        val attemptId = current.attemptId ?: return
        if (current.isSubmitting || current.isFinished) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, errorMessage = null)
            try {
                var submitError: Exception? = null
                try {
                    quizRepository.submitAttempt(attemptId)
                } catch (exception: Exception) {
                    submitError = exception
                }

                val detail = quizRepository.getAttempt(attemptId)
                if (detail.attempt.status != "submitted") {
                    throw submitError ?: Exception("Quiz could not be submitted")
                }

                storeResult(detail)
                timerJob?.cancel()
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    remainingSeconds = 0,
                    isFinished = true
                )
            } catch (exception: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    errorMessage = exception.message ?: "Failed to submit quiz"
                )
            }
        }
    }

    private fun storeResult(detail: AttemptDetailDto) {
        val attempt = detail.attempt
        val reviewItems = attempt.questions.map { question ->
            ReviewItem(
                questionText = question.questionText,
                userAnswer = question.answer?.userAnswer ?: "(no answer)",
                correctAnswer = question.getCorrectAnswer(),
                isCorrect = question.answer?.isCorrect == true
            )
        }
        QuizResultHolder.result = QuizResult(
            testName = attempt.collectionTest?.testName ?: _uiState.value.testName,
            score = attempt.correctCount,
            total = attempt.collectionTest?.totalQuestions ?: reviewItems.size,
            reviewItems = reviewItems
        )
    }

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
