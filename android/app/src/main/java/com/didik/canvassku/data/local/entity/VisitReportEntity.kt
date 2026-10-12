package com.didik.canvassku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "visit_reports")
data class VisitReportEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val storeId: String,
    val latitude: Double,
    val longitude: Double,
    val orderNotes: String,
    val localImagePath: String,
    val syncStatus: String,
    val createdAt: Long = System.currentTimeMillis(),
)

object SyncStatus {
    const val PENDING_SYNC = "PENDING_SYNC"
    const val SYNCED = "SYNCED"
    const val FAILED = "FAILED"
}
