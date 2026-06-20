package com.vu.englishlearningapp.data.repository

import com.vu.englishlearningapp.data.remote.api.QuizApi
import com.vu.englishlearningapp.data.remote.dto.quiz.CollectionTestDetailDto
import com.vu.englishlearningapp.data.remote.dto.quiz.CollectionTestDto

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
}
