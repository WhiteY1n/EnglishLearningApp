package com.vu.englishlearningapp.data.remote.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Wrapper for all API responses from the Laravel backend.
 * The backend always returns: status_code, message, data.
 */
data class ApiResponse<T>(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)
