package com.vu.englishlearningapp.data.repository

import com.vu.englishlearningapp.data.remote.api.QuestionApi
import com.vu.englishlearningapp.data.remote.dto.common.MetaDto
import com.vu.englishlearningapp.data.remote.dto.question.AdminQuestionDto
import com.vu.englishlearningapp.data.remote.dto.question.QuestionRequestDto
import com.vu.englishlearningapp.data.remote.dto.question.QuestionTypeDto

class QuestionRepository(private val questionApi: QuestionApi) {
    suspend fun getQuestions(
        page: Int,
        perPage: Int = 10,
        search: String? = null
    ): Pair<List<AdminQuestionDto>, MetaDto?> {
        val response = questionApi.getQuestions(page, perPage, search?.takeIf { it.isNotBlank() })
        return response.requireData() to response.meta
    }

    suspend fun getQuestion(id: Int): AdminQuestionDto =
        questionApi.getQuestion(id).requireData()

    suspend fun getQuestionTypes(): List<QuestionTypeDto> =
        questionApi.getQuestionTypes().requireData()

    suspend fun getAllQuestions(): List<AdminQuestionDto> {
        val allQuestions = mutableListOf<AdminQuestionDto>()
        var page = 1
        do {
            val (questions, meta) = getQuestions(page = page, perPage = 100)
            allQuestions += questions
            val lastPage = meta?.lastPage ?: page
            page++
        } while (page <= lastPage)
        return allQuestions.distinctBy { it.id }
    }

    suspend fun createQuestion(request: QuestionRequestDto): AdminQuestionDto =
        questionApi.createQuestion(request).requireData()

    suspend fun updateQuestion(id: Int, request: QuestionRequestDto): AdminQuestionDto =
        questionApi.updateQuestion(id, request).requireData()

    suspend fun deleteQuestion(id: Int) {
        val response = questionApi.deleteQuestion(id)
        if (response.statusCode !in 200..204) throw Exception(response.message)
    }

    private fun <T> com.vu.englishlearningapp.data.remote.dto.auth.ApiResponse<T>.requireData(): T {
        if (statusCode !in 200..299 || data == null) throw Exception(message)
        return data
    }
}
