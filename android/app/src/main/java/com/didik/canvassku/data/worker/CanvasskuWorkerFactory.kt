package com.didik.canvassku.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.didik.canvassku.domain.repository.VisitRepository
import org.koin.core.annotation.Singleton

@Singleton
class CanvasskuWorkerFactory(
    private val visitRepository: VisitRepository,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncReportWorker::class.java.name -> SyncReportWorker(
                appContext = appContext,
                params = workerParameters,
                visitRepository = visitRepository,
            )
            else -> null
        }
    }
}
