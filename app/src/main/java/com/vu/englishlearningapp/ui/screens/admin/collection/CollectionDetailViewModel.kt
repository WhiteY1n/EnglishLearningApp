package com.vu.englishlearningapp.ui.screens.admin.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDetailDto
import com.vu.englishlearningapp.data.repository.CollectionRepository
import com.vu.englishlearningapp.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CollectionDetailUiState(
    val collection: FlashcardCollectionDetailDto? = null,
    val isLoading: Boolean = false,
    val isDeleting: Boolean = false,
    val isDeleteSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CollectionDetailViewModel(
    private val collectionRepository: CollectionRepository,
    private val flashcardRepository: FlashcardRepository,
    private val collectionId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionDetailUiState())
    val uiState: StateFlow<CollectionDetailUiState> = _uiState.asStateFlow()

    init {
        loadCollectionDetail()
    }

    fun loadCollectionDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val detail = collectionRepository.getCollectionDetail(collectionId)
                _uiState.value = _uiState.value.copy(
                    collection = detail,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load collection details"
                )
            }
        }
    }

    fun deleteCollection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true, errorMessage = null)
            try {
                collectionRepository.deleteCollection(collectionId)
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    isDeleteSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    errorMessage = e.message ?: "Failed to delete collection"
                )
            }
        }
    }

    fun deleteFlashcard(flashcardId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                flashcardRepository.deleteFlashcard(flashcardId)
                // Reload the collection detail to reflect the deletion
                loadCollectionDetail()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to delete flashcard"
                )
            }
        }
    }

    class Factory(
        private val collectionRepository: CollectionRepository,
        private val flashcardRepository: FlashcardRepository,
        private val collectionId: Int
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CollectionDetailViewModel::class.java)) {
                return CollectionDetailViewModel(collectionRepository, flashcardRepository, collectionId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
