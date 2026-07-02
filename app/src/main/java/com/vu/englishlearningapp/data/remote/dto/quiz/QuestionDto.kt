package com.vu.englishlearningapp.data.remote.dto.quiz

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

/**
 * A single quiz question.
 *
 * The question_data field is stored as a raw JsonObject because:
 * - "correct" can be a String (for multiple choice) or Boolean (for true/false).
 * - Using JsonObject lets us safely handle both types without crashing.
 *
 * Use getOptions() and getCorrectAnswer() to safely extract values.
 */
data class QuestionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("question_data") val questionData: JsonObject,
    @SerializedName("flashcard_reference_ids") val flashcardReferenceIds: List<Int>,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("user_answer") val userAnswer: String? = null,
    @SerializedName("is_correct") val isCorrect: Boolean? = null
) {
    /**
     * Extract the options list from question_data.
     * Returns empty list if "options" key is not present.
     */
    fun getOptions(): List<String> {
        val optionsArray = questionData.getAsJsonArray("options") ?: return emptyList()
        return optionsArray.map { it.asString }
    }

    /**
     * Extract the correct answer from question_data.
     * Handles both String and Boolean values safely.
     */
    fun getCorrectAnswer(): String {
        val correct = questionData.get("correct") ?: return ""
        return when {
            correct.isJsonPrimitive -> {
                val primitive = correct.asJsonPrimitive
                when {
                    primitive.isString -> primitive.asString
                    primitive.isBoolean -> primitive.asBoolean.toString()
                    primitive.isNumber -> primitive.asNumber.toString()
                    else -> correct.toString()
                }
            }
            else -> correct.toString()
        }
    }
}
