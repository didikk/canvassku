package com.didik.canvassku.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.didik.canvassku.domain.model.SyncStatus
import java.util.UUID

@Entity(tableName = "visit_reports")
data class VisitReportEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val storeId: String,
    val latitude: Double,
    val longitude: Double,
    val orderNotes: String,
    val localImagePath: String,
    val syncStatus: SyncStatus,
    val createdAt: Long = System.currentTimeMillis(),
)
