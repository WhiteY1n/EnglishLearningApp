package com.vu.englishlearningapp.data.remote.api

import com.vu.englishlearningapp.data.remote.dto.auth.ApiResponse
import com.vu.englishlearningapp.data.remote.dto.auth.LoginRequest
import com.vu.englishlearningapp.data.remote.dto.auth.LoginResponse
import com.vu.englishlearningapp.data.remote.dto.auth.RefreshTokenRequest
import com.vu.englishlearningapp.data.remote.dto.auth.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Retrofit API interface for authentication endpoints.
 * Base path: /api/admin/auth/
 */
interface AuthApi {

    @POST("api/admin/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @GET("api/admin/auth/me")
    suspend fun me(): ApiResponse<UserDto>

    @POST("api/admin/auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @POST("api/admin/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<LoginResponse>
}
