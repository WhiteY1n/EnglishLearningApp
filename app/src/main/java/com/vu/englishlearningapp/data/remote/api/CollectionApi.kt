package com.vu.englishlearningapp.data.remote.api

import com.vu.englishlearningapp.data.remote.dto.auth.ApiResponse
import com.vu.englishlearningapp.data.remote.dto.flashcard.CollectionRequestDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDetailDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit API interface for admin flashcard collection management.
 */
interface CollectionApi {

    @GET("api/admin/flashcard-collections")
    suspend fun getCollections(): ApiResponse<List<FlashcardCollectionDto>>

    @GET("api/admin/flashcard-collections/{id}")
    suspend fun getCollectionDetail(@Path("id") id: Int): ApiResponse<FlashcardCollectionDetailDto>

    @POST("api/admin/flashcard-collections")
    suspend fun createCollection(@Body request: CollectionRequestDto): ApiResponse<FlashcardCollectionDetailDto>

    @PUT("api/admin/flashcard-collections/{id}")
    suspend fun updateCollection(
        @Path("id") id: Int,
        @Body request: CollectionRequestDto
    ): ApiResponse<FlashcardCollectionDetailDto>

    @DELETE("api/admin/flashcard-collections/{id}")
    suspend fun deleteCollection(@Path("id") id: Int): ApiResponse<Unit>
}
