package com.didik.canvassku.domain.repository

import com.didik.canvassku.domain.model.AuthSession

interface AuthRepository {
    suspend fun login(identifier: String, password: String): Result<AuthSession>
    suspend fun logout()
    suspend fun hasValidSession(): Boolean

    /** Set when ForceReLogin fires; survives process death until cleared after login. */
    suspend fun markSkipVisitSyncOnNextLogin()

    suspend fun isSkipVisitSyncOnNextLogin(): Boolean

    suspend fun clearSkipVisitSyncOnNextLogin()
}
