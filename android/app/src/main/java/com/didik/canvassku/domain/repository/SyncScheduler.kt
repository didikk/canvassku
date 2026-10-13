package com.didik.canvassku.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Schedules background upload of locally pending visit reports.
 * Implementation lives in data (WorkManager); domain stays free of Android APIs.
 */
interface SyncScheduler {
    fun enqueuePendingSync()

    fun cancelPendingSync()

    /** True while the unique report-sync work is actively RUNNING. */
    fun observeIsSyncing(): Flow<Boolean>
}
