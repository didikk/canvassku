package com.didik.canvassku.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.didik.canvassku.data.local.entity.VisitReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VisitReportDao {
    @Query("SELECT * FROM visit_reports ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<VisitReportEntity>>

    @Query("SELECT * FROM visit_reports WHERE id = :id LIMIT 1")
    fun observeById(id: String): Flow<VisitReportEntity?>

    @Query("SELECT * FROM visit_reports WHERE storeId = :storeId ORDER BY createdAt DESC")
    fun observeByStoreId(storeId: String): Flow<List<VisitReportEntity>>

    @Query("SELECT * FROM visit_reports WHERE syncStatus = :status")
    suspend fun getBySyncStatus(status: String): List<VisitReportEntity>

    @Query(
        """
        SELECT COUNT(*) FROM visit_reports
        WHERE syncStatus IN ('PENDING_SYNC', 'FAILED')
        """,
    )
    fun observePendingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(report: VisitReportEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(reports: List<VisitReportEntity>)

    @Query("DELETE FROM visit_reports")
    suspend fun clearAll()

    @Query("UPDATE visit_reports SET syncStatus = :status WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String)
}
