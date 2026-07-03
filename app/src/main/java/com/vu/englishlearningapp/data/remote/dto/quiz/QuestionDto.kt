package com.vu.englishlearningapp.data.remote.dto.quiz

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName

data class MatchingPairDto(val left: String, val right: String)

data class QuestionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("question_type_id") val questionTypeId: Int? = null,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("question_data") val questionData: JsonElement,
    @SerializedName("flashcard_reference_ids") val flashcardReferenceIds: List<Int> = emptyList(),
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("user_answer") val userAnswer: String? = null,
    @SerializedName("is_correct") val isCorrect: Boolean? = null
) {
    val typeKeyword: String
        get() {
            val data = getQuestionDataObject()
            return when {
                data.has("options") -> "multiple_choice"
                data.has("pairs") -> "matching"
                data.has("answer") -> "fill_in_blank"
                data.get("correct")?.isJsonPrimitive == true &&
                    data.getAsJsonPrimitive("correct").isBoolean -> "true_false"
                else -> "unknown"
            }
        }

    fun getQuestionDataObject(): JsonObject = questionData.toQuestionDataObject()

    fun getOptions(): List<String> =
        getQuestionDataObject().getAsJsonArray("options")?.map { it.asString }.orEmpty()

    fun getMatchingPairs(): List<MatchingPairDto> =
        getQuestionDataObject().getAsJsonArray("pairs")?.mapNotNull { element ->
            val pair = element.takeIf { it.isJsonObject }?.asJsonObject ?: return@mapNotNull null
            val left = pair.get("left")?.asString ?: return@mapNotNull null
            val right = pair.get("right")?.asString ?: return@mapNotNull null
            MatchingPairDto(left, right)
        }.orEmpty()

    fun getCorrectAnswer(): String {
        val data = getQuestionDataObject()
        data.get("answer")?.let { return it.asString }
        data.getAsJsonArray("pairs")?.let { return it.toString() }
        val correct = data.get("correct") ?: return ""
        if (typeKeyword == "multiple_choice" && correct.isJsonPrimitive && correct.asJsonPrimitive.isNumber) {
            return getOptions().getOrNull(correct.asInt) ?: correct.asString
        }
        return if (correct.isJsonPrimitive) correct.asString else correct.toString()
    }
}

internal fun JsonElement.toQuestionDataObject(): JsonObject {
    return when {
        isJsonObject -> asJsonObject
        isJsonPrimitive && asJsonPrimitive.isString -> runCatching {
            JsonParser.parseString(asString).asJsonObject
        }.getOrDefault(JsonObject())
        else -> JsonObject()
    }
}
