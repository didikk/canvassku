package com.didik.canvassku.domain.usecase

import com.didik.canvassku.domain.model.SyncStatus
import com.didik.canvassku.domain.model.VisitReport
import com.didik.canvassku.domain.repository.SyncScheduler
import com.didik.canvassku.domain.repository.VisitRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveVisitReportUseCaseTest {
    private val visitRepository: VisitRepository = mockk()
    private val syncScheduler: SyncScheduler = mockk(relaxUnitFun = true)
    private val useCase = SaveVisitReportUseCase(visitRepository, syncScheduler)

    @Test
    fun invoke_savesOfflineReport_withPendingSync_andEnqueuesWorker() = runTest {
        coEvery { visitRepository.saveLocal(any()) } returns Result.success(Unit)

        val result = useCase(
            storeId = "store-1",
            latitude = -6.2,
            longitude = 106.8,
            orderNotes = "  10 dus  ",
            localImagePath = "/files/photo.jpg",
        )

        assertTrue(result.isSuccess)
        val saved = result.getOrThrow()
        assertEquals(SyncStatus.PENDING_SYNC, saved.syncStatus)
        assertEquals("store-1", saved.storeId)
        assertEquals("10 dus", saved.orderNotes)
        assertEquals("/files/photo.jpg", saved.localImagePath)

        coVerify {
            visitRepository.saveLocal(
                match<VisitReport> { it.syncStatus == SyncStatus.PENDING_SYNC },
            )
        }
        verify { syncScheduler.enqueuePendingSync() }
    }

    @Test
    fun invoke_rejectsBlankNotes() = runTest {
        val result = useCase(
            storeId = "store-1",
            latitude = -6.2,
            longitude = 106.8,
            orderNotes = "   ",
            localImagePath = "/files/photo.jpg",
        )

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { visitRepository.saveLocal(any()) }
        verify(exactly = 0) { syncScheduler.enqueuePendingSync() }
    }
}
