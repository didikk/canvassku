package com.didik.canvassku.data.repository

import com.didik.canvassku.data.local.preferences.AuthPreferences
import com.didik.canvassku.data.remote.api.StrapiApi
import com.didik.canvassku.data.remote.dto.LoginRequestDto
import com.didik.canvassku.domain.model.AuthSession
import com.didik.canvassku.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class AuthRepositoryImpl(
    private val api: StrapiApi,
    private val authPreferences: AuthPreferences,
) : AuthRepository {
    override val session: Flow<AuthSession?> = authPreferences.session.map { prefs ->
        prefs?.let {
            AuthSession(
                jwtToken = it.jwtToken,
                refreshToken = it.refreshToken,
                tokenTimestamp = it.tokenTimestamp,
            )
        }
    }

    override suspend fun login(identifier: String, password: String): Result<AuthSession> {
        return try {
            val response = api.login(LoginRequestDto(identifier = identifier, password = password))
            val timestamp = System.currentTimeMillis()
            authPreferences.saveSession(
                jwtToken = response.jwt,
                refreshToken = response.refreshToken,
                tokenTimestamp = timestamp,
            )
            Result.success(
                AuthSession(
                    jwtToken = response.jwt,
                    refreshToken = response.refreshToken,
                    tokenTimestamp = timestamp,
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
        // Complete credentials required: JWT alone is not enough to recover after expiry.
        val session = authPreferences.session.first() ?: return false
        return session.jwtToken.isNotBlank() && !session.refreshToken.isNullOrBlank()
    }
}
