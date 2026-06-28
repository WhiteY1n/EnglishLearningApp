package com.vu.englishlearningapp.data.remote.dto.flashcard

import com.google.gson.annotations.SerializedName

/**
 * Represents a Word Type or Test Type used in dropdowns.
 */
data class WordTypeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("test_type") val testType: String, // from test-types endpoint
    @SerializedName("type_name") val typeName: String? = null // from flashcard resource nested word_type
) {
    val displayValue: String
        get() = typeName ?: testType
}
