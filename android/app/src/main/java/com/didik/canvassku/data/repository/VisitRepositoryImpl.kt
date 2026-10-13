package com.didik.canvassku.data.repository

import com.didik.canvassku.data.local.db.VisitReportDao
import com.didik.canvassku.data.local.entity.VisitReportEntity
import com.didik.canvassku.data.remote.api.StrapiApi
import com.didik.canvassku.data.remote.dto.VisitReportCreateData
import com.didik.canvassku.data.worker.SyncScheduler
import com.didik.canvassku.domain.model.SyncStatus
import com.didik.canvassku.domain.model.VisitReport
import com.didik.canvassku.domain.repository.VisitRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.core.annotation.Singleton
import retrofit2.HttpException
import java.io.File

@Singleton
class VisitRepositoryImpl(
    private val api: StrapiApi,
    private val visitReportDao: VisitReportDao,
    private val json: Json,
    private val syncScheduler: SyncScheduler,
) : VisitRepository {
    override suspend fun saveLocal(report: VisitReport): Result<Unit> {
        return try {
            visitReportDao.upsert(report.toEntity())
            syncScheduler.enqueueSync()
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override fun enqueuePendingSync() {
        syncScheduler.enqueueSync()
    }

    override suspend fun getPendingForSync(): List<VisitReport> {
        val pending = visitReportDao.getBySyncStatus(SyncStatus.PENDING_SYNC)
        val failed = visitReportDao.getBySyncStatus(SyncStatus.FAILED)
        return (pending + failed)
            .sortedBy { it.createdAt }
            .map { it.toDomain() }
    }

    override suspend fun markSynced(id: String) {
        visitReportDao.updateSyncStatus(id, SyncStatus.SYNCED)
    }

    override suspend fun markFailed(id: String) {
        visitReportDao.updateSyncStatus(id, SyncStatus.FAILED)
    }

    override suspend fun upload(report: VisitReport): Result<Unit> {
        val photoFile = File(report.localImagePath)
        if (!photoFile.exists()) {
            return Result.failure(IllegalStateException("File foto tidak ditemukan."))
        }

        val payload = VisitReportCreateData(
            store = report.storeId,
            latitude = report.latitude,
            longitude = report.longitude,
            orderNotes = report.orderNotes,
        )
        val dataBody = json.encodeToString(VisitReportCreateData.serializer(), payload)
            .toRequestBody(JSON_MEDIA_TYPE)
        val photoPart = MultipartBody.Part.createFormData(
            name = "files.photo",
            filename = photoFile.name,
            body = photoFile.asRequestBody(JPEG_MEDIA_TYPE),
        )

        return try {
            val response = api.createVisitReport(data = dataBody, photo = photoPart)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private fun VisitReport.toEntity(): VisitReportEntity = VisitReportEntity(
        id = id,
        storeId = storeId,
        latitude = latitude,
        longitude = longitude,
        orderNotes = orderNotes,
        localImagePath = localImagePath,
        syncStatus = syncStatus,
        createdAt = createdAt,
    )

    private fun VisitReportEntity.toDomain(): VisitReport = VisitReport(
        id = id,
        storeId = storeId,
        latitude = latitude,
        longitude = longitude,
        orderNotes = orderNotes,
        localImagePath = localImagePath,
        syncStatus = syncStatus,
        createdAt = createdAt,
    )

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
        val JPEG_MEDIA_TYPE = "image/jpeg".toMediaType()
    }
}
