package com.didik.canvassku.data.worker

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.didik.canvassku.domain.model.SyncStatus
import com.didik.canvassku.domain.model.VisitReport
import com.didik.canvassku.domain.repository.VisitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class SyncReportWorkerTest {
    private lateinit var context: Context
    private val visitRepository: VisitRepository = mockk(relaxUnitFun = true)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val config = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .setWorkerFactory(
                object : WorkerFactory() {
                    override fun createWorker(
                        appContext: Context,
                        workerClassName: String,
                        workerParameters: WorkerParameters,
                    ): ListenableWorker? {
                        if (workerClassName != SyncReportWorker::class.java.name) return null
                        return SyncReportWorker(appContext, workerParameters, visitRepository)
                    }
                },
            )
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    fun worker_runsUnderConnectedConstraint_andUploadsPendingReports() {
        val pending = VisitReport(
            id = "report-1",
            storeId = "store-1",
            latitude = -6.2,
            longitude = 106.8,
            orderNotes = "notes",
            localImagePath = "/files/a.jpg",
            syncStatus = SyncStatus.PENDING_SYNC,
            createdAt = 1L,
        )
        coEvery { visitRepository.getPendingForSync() } returns listOf(pending)
        coEvery { visitRepository.upload(pending) } returns Result.success(Unit)

        val request = OneTimeWorkRequestBuilder<SyncReportWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .build()

        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(request).result.get(5, TimeUnit.SECONDS)

        val before = workManager.getWorkInfoById(request.id).get(5, TimeUnit.SECONDS)!!
        assertTrue(
            before.state == WorkInfo.State.ENQUEUED || before.state == WorkInfo.State.BLOCKED,
        )

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)!!
        testDriver.setAllConstraintsMet(request.id)

        val workInfo = workManager.getWorkInfoById(request.id).get(5, TimeUnit.SECONDS)!!
        assertEquals(WorkInfo.State.SUCCEEDED, workInfo.state)
        coVerify { visitRepository.upload(pending) }
        coVerify { visitRepository.markSynced("report-1") }
    }
}
