package com.didik.canvassku.domain.usecase

import com.didik.canvassku.domain.model.SyncStatus
import com.didik.canvassku.domain.model.VisitReport
import com.didik.canvassku.domain.repository.SyncScheduler
import com.didik.canvassku.domain.repository.VisitRepository
import org.koin.core.annotation.Factory
import java.util.UUID

@Factory
class SaveVisitReportUseCase(
    private val visitRepository: VisitRepository,
    private val syncScheduler: SyncScheduler,
) {
    suspend operator fun invoke(
        storeId: String,
        latitude: Double,
        longitude: Double,
        orderNotes: String,
        localImagePath: String,
    ): Result<VisitReport> {
        val notes = orderNotes.trim()
        if (notes.isEmpty()) {
            return Result.failure(IllegalArgumentException("Catatan pesanan wajib diisi."))
        }
        if (localImagePath.isBlank()) {
            return Result.failure(IllegalArgumentException("Foto bukti kunjungan wajib diambil."))
        }

        val report = VisitReport(
            id = UUID.randomUUID().toString(),
            storeId = storeId,
            latitude = latitude,
            longitude = longitude,
            orderNotes = notes,
            localImagePath = localImagePath,
            syncStatus = SyncStatus.PENDING_SYNC,
            createdAt = System.currentTimeMillis(),
        )

        return visitRepository.saveLocal(report)
            .onSuccess { syncScheduler.enqueuePendingSync() }
            .map { report }
    }
}
