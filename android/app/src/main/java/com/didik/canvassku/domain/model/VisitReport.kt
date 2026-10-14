package com.didik.canvassku.domain.model

data class VisitReport(
    val id: String,
    val storeId: String,
    val latitude: Double,
    val longitude: Double,
    val orderNotes: String,
    val localImagePath: String,
    val syncStatus: SyncStatus,
    val createdAt: Long,
)
