package com.didik.canvassku.domain.usecase

import com.didik.canvassku.domain.model.AuthSession
import com.didik.canvassku.domain.repository.AuthRepository
import com.didik.canvassku.domain.repository.StoreRepository
import org.koin.core.annotation.Factory

@Factory
class LoginUseCase(
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository,
) {
    suspend operator fun invoke(identifier: String, password: String): Result<AuthSession> {
        val session = authRepository.login(identifier, password)
            .getOrElse { return Result.failure(it) }

        // Seed Room SSOT after login; failure still keeps the session so Home can retry sync.
        storeRepository.syncStores()
        return Result.success(session)
    }
}
