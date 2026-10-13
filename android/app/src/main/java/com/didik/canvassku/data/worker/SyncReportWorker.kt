package com.didik.canvassku.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.didik.canvassku.domain.repository.VisitRepository
import retrofit2.HttpException
import java.io.IOException

/**
 * Uploads PENDING_SYNC / FAILED visit reports when network is available.
 * shortcut: token refresh is handled by TokenAuthenticator on 401, upgrade if worker must
 * fail-fast before the first multipart upload.
 */
class SyncReportWorker(
    appContext: Context,
    params: WorkerParameters,
    private val visitRepository: VisitRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val pending = visitRepository.getPendingForSync()
        if (pending.isEmpty()) return Result.success()

        var hadTransientFailure = false
        for (report in pending) {
            visitRepository.upload(report)
                .onSuccess {
                    visitRepository.markSynced(report.id)
                }
                .onFailure { error ->
                    when {
                        error is HttpException && error.code() in 400..499 && error.code() != 408 -> {
                            // Auth expiry triggers ForceReLogin elsewhere; keep report pending.
                            if (error.code() == 401 || error.code() == 403) {
                                return Result.retry()
                            }
                            visitRepository.markFailed(report.id)
                        }
                        error is IOException || error is HttpException -> {
                            visitRepository.markFailed(report.id)
                            hadTransientFailure = true
                        }
                        else -> {
                            visitRepository.markFailed(report.id)
                            hadTransientFailure = true
                        }
                    }
                }
        }

        return if (hadTransientFailure) Result.retry() else Result.success()
    }
}
