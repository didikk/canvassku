package com.didik.canvassku.domain.model

data class StoreListItem(
    val store: Store,
    val status: SyncStatus,
    val latestReportId: String? = null,
    val latestReportCreatedAt: Long? = null,
)
