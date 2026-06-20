package com.vu.englishlearningapp.data.remote.api

import com.vu.englishlearningapp.data.remote.dto.auth.ApiResponse
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDetailDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API interface for flashcard endpoints.
 */
interface FlashcardApi {

    @GET("api/admin/flashcard-collections")
    suspend fun getCollections(): ApiResponse<List<FlashcardCollectionDto>>

    @GET("api/admin/flashcard-collections/{id}")
    suspend fun getCollectionDetail(@Path("id") id: Int): ApiResponse<FlashcardCollectionDetailDto>
}
