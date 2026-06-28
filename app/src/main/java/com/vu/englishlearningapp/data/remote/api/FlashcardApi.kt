package com.vu.englishlearningapp.data.remote.api

import com.vu.englishlearningapp.data.remote.dto.auth.ApiResponse
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDetailDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardRequestDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.WordTypeDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.AttachFlashcardsRequestDto
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Body

/**
 * Retrofit API interface for flashcard endpoints.
 */
interface FlashcardApi {

    @GET("api/admin/flashcard-collections")
    suspend fun getCollections(): ApiResponse<List<FlashcardCollectionDto>>

    @GET("api/admin/flashcard-collections/{id}")
    suspend fun getCollectionDetail(@Path("id") id: Int): ApiResponse<FlashcardCollectionDetailDto>

    // --- Flashcard Operations ---

    @GET("api/admin/flashcards/{id}")
    suspend fun getFlashcard(@Path("id") id: Int): ApiResponse<FlashcardDto>

    @POST("api/admin/flashcards")
    suspend fun createFlashcard(@Body request: FlashcardRequestDto): ApiResponse<FlashcardDto>

    @PUT("api/admin/flashcards/{id}")
    suspend fun updateFlashcard(
        @Path("id") id: Int,
        @Body request: FlashcardRequestDto
    ): ApiResponse<FlashcardDto>

    @DELETE("api/admin/flashcards/{id}")
    suspend fun deleteFlashcard(@Path("id") id: Int): ApiResponse<Unit>

    // --- Word Types / Test Types ---

    // Note: The backend uses `test-types` for this endpoint but we map it to WordTypeDto
    @GET("api/admin/test-types")
    suspend fun getTestTypes(): ApiResponse<List<WordTypeDto>>

    // --- Collection Attach Operations ---

    @POST("api/admin/flashcard-collections/{id}/attach")
    suspend fun attachFlashcardsToCollection(
        @Path("id") collectionId: Int,
        @Body request: AttachFlashcardsRequestDto
    ): ApiResponse<Unit>
}
