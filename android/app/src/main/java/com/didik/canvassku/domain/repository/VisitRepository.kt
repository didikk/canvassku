package com.didik.canvassku.domain.repository

import com.didik.canvassku.domain.model.VisitReport

interface VisitRepository {
    suspend fun saveLocal(report: VisitReport): Result<Unit>
    fun enqueuePendingSync()
    suspend fun getPendingForSync(): List<VisitReport>
    suspend fun markSynced(id: String)
    suspend fun markFailed(id: String)
    suspend fun upload(report: VisitReport): Result<Unit>
}
