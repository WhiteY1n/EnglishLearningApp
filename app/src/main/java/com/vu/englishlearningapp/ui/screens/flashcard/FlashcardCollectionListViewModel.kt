package com.vu.englishlearningapp.ui.screens.flashcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.vu.englishlearningapp.data.remote.dto.flashcard.FlashcardCollectionDto
import com.vu.englishlearningapp.data.repository.FlashcardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the flashcard collection list screen.
 */
data class CollectionListUiState(
    val collections: List<FlashcardCollectionDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for displaying the list of flashcard collections.
 */
class FlashcardCollectionListViewModel(
    private val flashcardRepository: FlashcardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollectionListUiState())
    val uiState: StateFlow<CollectionListUiState> = _uiState.asStateFlow()

    init {
        loadCollections()
    }

    /**
     * Load all flashcard collections from the API.
     */
    fun loadCollections() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val collections = flashcardRepository.getCollections()
                _uiState.value = _uiState.value.copy(
                    collections = collections,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load collections"
                )
            }
        }
    }

    /**
     * Factory for creating this ViewModel with dependencies.
     */
    class Factory(
        private val flashcardRepository: FlashcardRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FlashcardCollectionListViewModel::class.java)) {
                return FlashcardCollectionListViewModel(flashcardRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
