package com.didik.canvassku.domain.usecase

import com.didik.canvassku.domain.repository.AuthRepository
import com.didik.canvassku.domain.repository.StoreRepository
import com.didik.canvassku.domain.repository.VisitRepository
import org.koin.core.annotation.Factory

@Factory
class LoginUseCase(
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository,
    private val visitRepository: VisitRepository,
) {
    suspend operator fun invoke(
        identifier: String,
        password: String,
    ): Result<Unit> {
        val skipVisitPull = authRepository.isSkipVisitSyncOnNextLogin()

        authRepository.login(identifier, password)
            .getOrElse { return Result.failure(it) }

        // Seed Room SSOT after login; failure still keeps the session so Home can retry sync.
        storeRepository.syncStores()
        if (!skipVisitPull) {
            visitRepository.syncFromRemote()
        }
        authRepository.clearSkipVisitSyncOnNextLogin()
        return Result.success(Unit)
    }
}
