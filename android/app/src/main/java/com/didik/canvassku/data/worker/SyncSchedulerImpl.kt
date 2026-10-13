package com.didik.canvassku.data.worker

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.didik.canvassku.domain.repository.SyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
class SyncSchedulerImpl(
    private val workManager: WorkManager,
) : SyncScheduler {
    override fun enqueuePendingSync() {
        val request = OneTimeWorkRequestBuilder<SyncReportWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .build()
        workManager.enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    override fun cancelPendingSync() {
        workManager.cancelUniqueWork(UNIQUE_WORK_NAME)
    }

    override fun observeIsSyncing(): Flow<Boolean> =
        workManager.getWorkInfosForUniqueWorkFlow(UNIQUE_WORK_NAME)
            .map { infos -> infos.any { it.state == WorkInfo.State.RUNNING } }

    companion object {
        const val UNIQUE_WORK_NAME = "sync_visit_reports"
    }
}
