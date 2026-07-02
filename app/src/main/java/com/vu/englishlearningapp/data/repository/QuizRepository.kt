package com.vu.englishlearningapp.data.repository

import com.vu.englishlearningapp.data.remote.api.QuizApi
import com.vu.englishlearningapp.data.remote.dto.quiz.CollectionTestDetailDto
import com.vu.englishlearningapp.data.remote.dto.quiz.CollectionTestDto
import com.vu.englishlearningapp.data.remote.dto.quiz.AttemptDetailDto
import com.vu.englishlearningapp.data.remote.dto.quiz.QuestionDto
import com.vu.englishlearningapp.data.remote.dto.quiz.SaveAnswerRequestDto
import com.vu.englishlearningapp.data.remote.dto.quiz.StartAttemptDto
import com.vu.englishlearningapp.data.remote.dto.quiz.AttemptHistoryDto
import com.vu.englishlearningapp.data.remote.dto.common.MetaDto

/**
 * Repository for quiz/test operations.
 * Calls QuizApi and handles response validation.
 */
class QuizRepository(private val quizApi: QuizApi) {

    /**
     * Get all available tests.
     */
    suspend fun getTests(): List<CollectionTestDto> {
        val response = quizApi.getTests()
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    /**
     * Get a single test with all its questions.
     */
    suspend fun getTestDetail(id: Int): CollectionTestDetailDto {
        val response = quizApi.getTestDetail(id)
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun startAttempt(testId: Int): StartAttemptDto {
        val response = quizApi.startAttempt(testId)
        return response.requireData()
    }

    suspend fun getAttempt(attemptId: Int): AttemptDetailDto {
        val response = quizApi.getAttempt(attemptId)
        return response.requireData()
    }

    suspend fun getAttemptQuestions(attemptId: Int): List<QuestionDto> {
        val response = quizApi.getAttemptQuestions(attemptId)
        return response.requireData()
    }

    suspend fun saveAnswer(attemptId: Int, questionId: Int, userAnswer: String) {
        val response = quizApi.saveAnswer(
            attemptId = attemptId,
            request = SaveAnswerRequestDto(questionId, userAnswer)
        )
        if (response.statusCode !in 200..299) {
            throw Exception(response.message)
        }
    }

    suspend fun submitAttempt(attemptId: Int) {
        val response = quizApi.submitAttempt(attemptId)
        if (response.statusCode !in 200..299) {
            throw Exception(response.message)
        }
    }

    suspend fun getMyAttempts(page: Int): Pair<List<AttemptHistoryDto>, MetaDto?> {
        val response = quizApi.getMyAttempts(page)
        val attempts = response.requireData()
        return attempts to response.meta
    }

    private fun <T> com.vu.englishlearningapp.data.remote.dto.auth.ApiResponse<T>.requireData(): T {
        if (statusCode !in 200..299 || data == null) {
            throw Exception(message)
        }
        return data
    }
}
