package com.vu.englishlearningapp.data.remote.dto.quiz

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class StartAttemptDto(
    @SerializedName("attempt_id") val attemptId: Int,
    @SerializedName("expires_at") val expiresAt: String?,
    @SerializedName("remaining_seconds") val remainingSeconds: Int? = null
)

data class AttemptDetailDto(
    @SerializedName("attempt") val attempt: AttemptDto,
    @SerializedName("remaining_seconds") val remainingSeconds: Int
)

data class AttemptDto(
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: String,
    @SerializedName("correct_count") val correctCount: Int,
    @SerializedName("total_score") val totalScore: Int,
    @SerializedName("started_time") val startedTime: String?,
    @SerializedName("finished_time") val finishedTime: String?,
    @SerializedName("total_time") val totalTime: String?,
    @SerializedName("expired_at") val expiresAt: String?,
    @SerializedName("collection_test") val collectionTest: AttemptTestDto?,
    @SerializedName("questions") val questions: List<AttemptQuestionDto> = emptyList()
)

data class AttemptTestDto(
    @SerializedName("id") val id: Int,
    @SerializedName("test_name") val testName: String,
    @SerializedName("total_questions") val totalQuestions: Int,
    @SerializedName("duration") val duration: Int
)

data class AttemptQuestionDto(
    @SerializedName("id") val id: Int,
    @SerializedName("question_text") val questionText: String,
    @SerializedName("question_data") val questionData: JsonObject,
    @SerializedName("answer") val answer: AttemptAnswerDto?
) {
    fun getCorrectAnswer(): String {
        val correct = questionData.get("correct") ?: return ""
        return if (correct.isJsonPrimitive) correct.asJsonPrimitive.asString else correct.toString()
    }
}

data class AttemptAnswerDto(
    @SerializedName("user_answer") val userAnswer: String,
    @SerializedName("is_correct") val isCorrect: Boolean
)

data class SaveAnswerRequestDto(
    @SerializedName("question_id") val questionId: Int,
    @SerializedName("user_answer") val userAnswer: String
)

data class AttemptHistoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: String,
    @SerializedName("collection_test") val collectionTest: AttemptTestDto?,
    @SerializedName("result") val result: AttemptHistoryResultDto,
    @SerializedName("started_at") val startedAt: String?,
    @SerializedName("submitted_at") val submittedAt: String?,
    @SerializedName("total_time") val totalTime: String?
)

data class AttemptHistoryResultDto(
    @SerializedName("correct_count") val correctCount: Int,
    @SerializedName("total_score") val totalScore: Int
)
