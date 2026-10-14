package com.didik.canvassku.domain.repository

interface AuthRepository {
    suspend fun login(identifier: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun hasValidSession(): Boolean

    /** Set when session expires; survives process death until cleared after login. */
    suspend fun markSkipVisitSyncOnNextLogin()

    suspend fun isSkipVisitSyncOnNextLogin(): Boolean

    suspend fun clearSkipVisitSyncOnNextLogin()
}
