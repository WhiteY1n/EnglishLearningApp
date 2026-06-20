package com.vu.englishlearningapp.data.remote.api

import com.vu.englishlearningapp.data.remote.dto.auth.ApiResponse
import com.vu.englishlearningapp.data.remote.dto.quiz.CollectionTestDetailDto
import com.vu.englishlearningapp.data.remote.dto.quiz.CollectionTestDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API interface for quiz/test endpoints.
 */
interface QuizApi {

    @GET("api/admin/collection-tests")
    suspend fun getTests(): ApiResponse<List<CollectionTestDto>>

    @GET("api/admin/collection-tests/{id}")
    suspend fun getTestDetail(@Path("id") id: Int): ApiResponse<CollectionTestDetailDto>
}
