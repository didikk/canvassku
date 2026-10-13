package com.didik.canvassku.domain.repository

import com.didik.canvassku.domain.model.VisitReport
import kotlinx.coroutines.flow.Flow

interface VisitRepository {
    fun observeById(id: String): Flow<VisitReport?>
    suspend fun saveLocal(report: VisitReport): Result<Unit>
    suspend fun getPendingForSync(): List<VisitReport>
    suspend fun markSynced(id: String)
    suspend fun markFailed(id: String)
    suspend fun upload(report: VisitReport): Result<Unit>

    /** Pull remote reports into Room as SYNCED (after logout wipe / fresh login). */
    suspend fun syncFromRemote(): Result<Unit>
}
