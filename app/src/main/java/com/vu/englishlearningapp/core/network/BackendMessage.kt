package com.vu.englishlearningapp.core.network

import com.google.gson.JsonParser
import com.vu.englishlearningapp.data.remote.dto.auth.ApiResponse
import retrofit2.HttpException

data class BackendResult<T>(
    val data: T,
    val message: String
)

data class BackendActionResult(
    val message: String
)

fun <T> ApiResponse<T>.requireBackendData(): BackendResult<T> {
    if (statusCode !in 200..299 || data == null) throw BackendMessageException(message)
    return BackendResult(data = data, message = message)
}

fun ApiResponse<*>.requireBackendSuccess(): BackendActionResult {
    if (statusCode !in 200..299) throw BackendMessageException(message)
    return BackendActionResult(message)
}

fun Throwable.toBackendMessage(): String {
    if (this is BackendMessageException) return message.orEmpty()
    if (this is HttpException) {
        val backendMessage = runCatching {
            val body = response()?.errorBody()?.string().orEmpty()
            JsonParser.parseString(body).asJsonObject.get("message")?.asString
        }.getOrNull()
        if (!backendMessage.isNullOrBlank()) return backendMessage
    }
    return localizedMessage ?: message ?: javaClass.simpleName
}

private class BackendMessageException(message: String) : Exception(message)
