package com.didik.canvassku.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.didik.canvassku.data.local.entity.VisitReportEntity
import com.didik.canvassku.domain.model.SyncStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class VisitReportDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var visitReportDao: VisitReportDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        visitReportDao = database.visitReportDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun updateSyncStatus_transitionsPendingToSynced_viaTypeConverter() = runTest {
        val report = VisitReportEntity(
            id = "report-1",
            storeId = "store-1",
            latitude = -6.2,
            longitude = 106.8,
            orderNotes = "catatan",
            localImagePath = "/files/a.jpg",
            syncStatus = SyncStatus.PENDING_SYNC,
            createdAt = 100L,
        )
        visitReportDao.upsert(report)

        assertEquals(
            listOf(report),
            visitReportDao.getBySyncStatus(SyncStatus.PENDING_SYNC),
        )

        visitReportDao.updateSyncStatus("report-1", SyncStatus.SYNCED)

        assertEquals(emptyList<VisitReportEntity>(), visitReportDao.getBySyncStatus(SyncStatus.PENDING_SYNC))
        val synced = visitReportDao.observeById("report-1").first()
        assertEquals(SyncStatus.SYNCED, synced?.syncStatus)
        assertEquals(0, visitReportDao.observePendingCount().first())
    }
}
