package com.didik.canvassku.presentation.screens.visit_form

import com.didik.canvassku.core.util.GeoPoint
import com.didik.canvassku.core.util.ImageCompressor
import com.didik.canvassku.core.util.LocationHelper
import com.didik.canvassku.domain.model.SyncStatus
import com.didik.canvassku.domain.model.VisitReport
import com.didik.canvassku.domain.repository.StoreRepository
import com.didik.canvassku.domain.usecase.SaveVisitReportUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class VisitFormViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val storeRepository: StoreRepository = mockk()
    private val locationHelper: LocationHelper = mockk()
    private val imageCompressor: ImageCompressor = mockk()
    private val saveVisitReportUseCase: SaveVisitReportUseCase = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        coEvery { storeRepository.observeStore(any()) } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun submit_callsSaveUseCase_andEmitsNavigateBack() = runTest(dispatcher) {
        coEvery { locationHelper.lockCurrentLocation() } returns
            Result.success(GeoPoint(latitude = -6.2, longitude = 106.8))
        val compressed = File.createTempFile("visit", ".jpg")
        coEvery { imageCompressor.compressToFilesDir(any()) } returns compressed
        coEvery {
            saveVisitReportUseCase(
                storeId = "store-1",
                latitude = -6.2,
                longitude = 106.8,
                orderNotes = "5 karton",
                localImagePath = compressed.absolutePath,
            )
        } returns Result.success(
            VisitReport(
                id = "report-1",
                storeId = "store-1",
                latitude = -6.2,
                longitude = 106.8,
                orderNotes = "5 karton",
                localImagePath = compressed.absolutePath,
                syncStatus = SyncStatus.PENDING_SYNC,
                createdAt = 1L,
            ),
        )

        val viewModel = VisitFormViewModel(
            storeId = "store-1",
            storeRepository = storeRepository,
            locationHelper = locationHelper,
            imageCompressor = imageCompressor,
            saveVisitReportUseCase = saveVisitReportUseCase,
        )
        assertEquals(VisitFormEffect.RequestLocationPermission, viewModel.effect.first())

        viewModel.onLocationPermissionResult(granted = true)
        advanceUntilIdle()
        viewModel.onOrderNotesChange("5 karton")
        viewModel.onPhotoCaptured(File.createTempFile("raw", ".jpg").absolutePath)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.location is VisitFormState.LocationUi.Ready)
        assertEquals(VisitFormState.Status.Idle, viewModel.uiState.value.status)

        viewModel.onSubmitClick()
        advanceUntilIdle()

        coVerify {
            saveVisitReportUseCase(
                storeId = "store-1",
                latitude = -6.2,
                longitude = 106.8,
                orderNotes = "5 karton",
                localImagePath = compressed.absolutePath,
            )
        }
        // Production navigates home on save; no Success status (stays Saving → NavigateBack).
        assertEquals(VisitFormState.Status.Saving, viewModel.uiState.value.status)
        assertEquals(VisitFormEffect.NavigateBack, viewModel.effect.first())
    }
}
