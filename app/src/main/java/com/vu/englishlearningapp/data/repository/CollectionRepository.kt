package com.vu.englishlearningapp.data.repository

import com.vu.englishlearningapp.data.remote.api.CollectionApi
import com.vu.englishlearningapp.data.remote.dto.flashcard.CollectionRequestDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDetailDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDto

/**
 * Repository for Admin flashcard collection management.
 */
class CollectionRepository(private val collectionApi: CollectionApi) {

    suspend fun getCollections(): List<FlashcardCollectionDto> {
        val response = collectionApi.getCollections()
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun getCollectionDetail(id: Int): FlashcardCollectionDetailDto {
        val response = collectionApi.getCollectionDetail(id)
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun createCollection(name: String, description: String?): FlashcardCollectionDetailDto {
        val response = collectionApi.createCollection(CollectionRequestDto(name, description))
        if (response.statusCode !in 200..201 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun updateCollection(id: Int, name: String, description: String?): FlashcardCollectionDetailDto {
        val response = collectionApi.updateCollection(id, CollectionRequestDto(name, description))
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun deleteCollection(id: Int) {
        val response = collectionApi.deleteCollection(id)
        if (response.statusCode !in 200..204) {
            throw Exception(response.message)
        }
    }
}
