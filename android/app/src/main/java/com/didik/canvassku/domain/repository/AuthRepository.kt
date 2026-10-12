package com.didik.canvassku.domain.repository

import com.didik.canvassku.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val session: Flow<AuthSession?>
    suspend fun login(identifier: String, password: String): Result<AuthSession>
    suspend fun logout()
    suspend fun hasValidSession(): Boolean
}
