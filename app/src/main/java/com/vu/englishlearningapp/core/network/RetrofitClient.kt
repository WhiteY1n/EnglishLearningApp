package com.vu.englishlearningapp.core.network

import com.vu.englishlearningapp.core.session.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton-like factory for creating Retrofit instances.
 * Configures OkHttp with logging and auth interceptors.
 */
object RetrofitClient {

    // Base URL for the Android emulator to reach the host machine's localhost
    private const val BASE_URL = "http://10.0.2.2:8000/"

    /**
     * Creates a configured Retrofit instance.
     *
     * @param tokenManager Used by AuthInterceptor to attach Bearer tokens.
     * @return A fully configured Retrofit instance.
     */
    fun create(tokenManager: TokenManager): Retrofit {
        // Logging interceptor — logs request/response bodies in debug builds
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Auth interceptor — adds Bearer token to requests
        val authInterceptor = AuthInterceptor(tokenManager)

        // Build OkHttpClient with both interceptors
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)       // Runs first: adds auth header
            .addInterceptor(loggingInterceptor)     // Runs second: logs the full request
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Build and return Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
