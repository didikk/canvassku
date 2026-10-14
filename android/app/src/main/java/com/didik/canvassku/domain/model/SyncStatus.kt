package com.didik.canvassku.domain.model

/** Report sync state; [NOT_VISITED] is list-only (never persisted on a report). */
enum class SyncStatus {
    NOT_VISITED,
    PENDING_SYNC,
    SYNCED,
    FAILED,
}
