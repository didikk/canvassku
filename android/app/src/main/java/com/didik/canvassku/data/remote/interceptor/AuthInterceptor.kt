package com.didik.canvassku.data.remote.interceptor

import com.didik.canvassku.data.local.preferences.AuthPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.koin.core.annotation.Singleton

@Singleton
class AuthInterceptor(
    private val authPreferences: AuthPreferences,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.isAuthExempt()) {
            return chain.proceed(request)
        }

        val token = authPreferences.peekJwtToken()
            ?: runBlocking { authPreferences.currentJwtToken() }
        if (token.isNullOrBlank()) {
            return chain.proceed(request)
        }

        val authenticated = request.newBuilder()
            .header(HEADER_AUTHORIZATION, "Bearer $token")
            .build()
        return chain.proceed(authenticated)
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}

internal fun okhttp3.Request.isAuthExempt(): Boolean {
    val path = url.encodedPath
    return path.contains("/api/auth/local") || path.contains("/api/auth/refresh")
}
