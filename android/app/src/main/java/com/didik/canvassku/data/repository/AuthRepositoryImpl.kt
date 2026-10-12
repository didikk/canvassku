package com.didik.canvassku.data.repository

import com.didik.canvassku.data.local.preferences.AuthPreferences
import com.didik.canvassku.data.remote.api.StrapiApi
import com.didik.canvassku.data.remote.dto.LoginRequestDto
import com.didik.canvassku.domain.model.AuthSession
import com.didik.canvassku.domain.repository.AuthRepository
import org.koin.core.annotation.Singleton

@Singleton
class AuthRepositoryImpl(
    private val api: StrapiApi,
    private val authPreferences: AuthPreferences,
) : AuthRepository {
    override suspend fun login(identifier: String, password: String): Result<AuthSession> {
        return try {
            val response = api.login(LoginRequestDto(identifier = identifier, password = password))
            authPreferences.saveSession(
                jwtToken = response.jwt,
                refreshToken = response.refreshToken,
            )
            Result.success(
                AuthSession(
                    jwtToken = response.jwt,
                    refreshToken = response.refreshToken,
                ),
            )
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override suspend fun logout() {
        authPreferences.clearSession()
    }

    override suspend fun hasValidSession(): Boolean {
        val jwt = authPreferences.currentJwtToken()
        val refresh = authPreferences.currentRefreshToken()
        return !jwt.isNullOrBlank() && !refresh.isNullOrBlank()
    }
}
