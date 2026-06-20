package com.vu.englishlearningapp.di

import android.content.Context
import com.vu.englishlearningapp.core.network.RetrofitClient
import com.vu.englishlearningapp.core.session.TokenManager
import com.vu.englishlearningapp.data.remote.api.AuthApi
import com.vu.englishlearningapp.data.repository.AuthRepository

/**
 * Manual dependency injection container.
 * Creates and holds all shared dependencies for the app.
 *
 * This is a simple alternative to Hilt/Dagger for beginners.
 * All dependencies are created lazily (only when first accessed).
 */
class AppContainer(context: Context) {

    // --- Core ---
    val tokenManager = TokenManager(context)

    // --- Network ---
    private val retrofit = RetrofitClient.create(tokenManager)

    // --- API Services ---
    val authApi: AuthApi = retrofit.create(AuthApi::class.java)

    // --- Repositories ---
    val authRepository = AuthRepository(authApi, tokenManager)
}
