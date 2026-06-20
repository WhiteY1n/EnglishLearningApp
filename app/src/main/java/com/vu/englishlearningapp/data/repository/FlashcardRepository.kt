package com.vu.englishlearningapp.data.repository

import com.vu.englishlearningapp.data.remote.api.FlashcardApi
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDetailDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDto

/**
 * Repository for flashcard operations.
 * Calls FlashcardApi and handles response validation.
 */
class FlashcardRepository(private val flashcardApi: FlashcardApi) {

    /**
     * Get all flashcard collections.
     */
    suspend fun getCollections(): List<FlashcardCollectionDto> {
        val response = flashcardApi.getCollections()
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    /**
     * Get a single collection with all its flashcards.
     */
    suspend fun getCollectionDetail(id: Int): FlashcardCollectionDetailDto {
        val response = flashcardApi.getCollectionDetail(id)
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }
}
