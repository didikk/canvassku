package com.didik.canvassku.data.repository

import com.didik.canvassku.data.local.db.AppDatabase
import com.didik.canvassku.data.local.preferences.AuthPreferences
import com.didik.canvassku.data.remote.api.StrapiApi
import com.didik.canvassku.data.remote.dto.LoginRequestDto
import com.didik.canvassku.domain.repository.AuthRepository
import com.didik.canvassku.domain.repository.SyncScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Singleton

@Singleton
class AuthRepositoryImpl(
    private val api: StrapiApi,
    private val authPreferences: AuthPreferences,
    private val appDatabase: AppDatabase,
    private val syncScheduler: SyncScheduler,
) : AuthRepository {
    override suspend fun login(identifier: String, password: String): Result<Unit> {
        return try {
            val response = api.login(LoginRequestDto(identifier = identifier, password = password))
            authPreferences.saveSession(
                jwtToken = response.jwt,
                refreshToken = response.refreshToken,
            )
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override suspend fun logout() {
        syncScheduler.cancelPendingSync()
        withContext(Dispatchers.IO) {
            appDatabase.clearAllTables()
        }
        authPreferences.clearSkipVisitSyncOnNextLogin()
        authPreferences.clearSession()
    }

    override suspend fun hasValidSession(): Boolean {
        val jwt = authPreferences.currentJwtToken()
        val refresh = authPreferences.currentRefreshToken()
        return !jwt.isNullOrBlank() && !refresh.isNullOrBlank()
    }

    override suspend fun markSkipVisitSyncOnNextLogin() {
        authPreferences.markSkipVisitSyncOnNextLogin()
    }

    override suspend fun isSkipVisitSyncOnNextLogin(): Boolean =
        authPreferences.isSkipVisitSyncOnNextLogin()

    override suspend fun clearSkipVisitSyncOnNextLogin() {
        authPreferences.clearSkipVisitSyncOnNextLogin()
    }
}
