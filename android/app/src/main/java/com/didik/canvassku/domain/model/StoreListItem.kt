package com.didik.canvassku.domain.model

enum class StoreVisitStatus {
    NOT_VISITED,
    PENDING_SYNC,
    SYNCED,
}

data class StoreListItem(
    val store: Store,
    val status: StoreVisitStatus,
    val latestReportId: String? = null,
    val statusDetail: String? = null,
)
