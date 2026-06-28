package com.vu.englishlearningapp.data.repository

import com.vu.englishlearningapp.data.remote.api.FlashcardApi
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDetailDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardRequestDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.WordTypeDto
import com.vu.englishlearningapp.data.remote.dto.flashcard.AttachFlashcardsRequestDto

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

    // --- Flashcard Operations ---

    suspend fun getFlashcard(id: Int): FlashcardDto {
        val response = flashcardApi.getFlashcard(id)
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun createFlashcard(originalWord: String, translatedWord: String, wordTypeId: Int): FlashcardDto {
        val response = flashcardApi.createFlashcard(FlashcardRequestDto(originalWord, translatedWord, wordTypeId))
        if (response.statusCode !in 200..201 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun updateFlashcard(id: Int, originalWord: String, translatedWord: String, wordTypeId: Int): FlashcardDto {
        val response = flashcardApi.updateFlashcard(id, FlashcardRequestDto(originalWord, translatedWord, wordTypeId))
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    suspend fun deleteFlashcard(id: Int) {
        val response = flashcardApi.deleteFlashcard(id)
        if (response.statusCode !in 200..204) {
            throw Exception(response.message)
        }
    }

    // --- Word Types ---

    suspend fun getWordTypes(): List<WordTypeDto> {
        // TODO: Update endpoint to /api/admin/word-types when backend is fixed.
        // Currently falling back to test-types due to backend dependency.
        val response = flashcardApi.getTestTypes()
        if (response.statusCode != 200 || response.data == null) {
            throw Exception(response.message)
        }
        return response.data
    }

    // --- Collection Attach Operations ---

    suspend fun attachToCollection(collectionId: Int, flashcardIds: List<Int>) {
        val response = flashcardApi.attachFlashcardsToCollection(collectionId, AttachFlashcardsRequestDto(flashcardIds))
        if (response.statusCode != 200) {
            throw Exception(response.message)
        }
    }
}
