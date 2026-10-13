package com.didik.canvassku.data.repository

import androidx.room.withTransaction
import com.didik.canvassku.data.local.db.AppDatabase
import com.didik.canvassku.data.local.db.VisitReportDao
import com.didik.canvassku.data.local.entity.VisitReportEntity
import com.didik.canvassku.data.remote.api.StrapiApi
import com.didik.canvassku.data.remote.dto.VisitReportCreateData
import com.didik.canvassku.data.remote.dto.VisitReportCreateRequest
import com.didik.canvassku.data.remote.dto.VisitReportDto
import com.didik.canvassku.domain.model.SyncStatus
import com.didik.canvassku.domain.model.VisitReport
import com.didik.canvassku.domain.repository.VisitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.koin.core.annotation.Singleton
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Singleton
class VisitRepositoryImpl(
    private val api: StrapiApi,
    private val appDatabase: AppDatabase,
    private val visitReportDao: VisitReportDao,
    private val baseUrl: String,
) : VisitRepository {
    override fun observeById(id: String): Flow<VisitReport?> {
        return visitReportDao.observeById(id).map { entity -> entity?.toDomain() }
    }

    override suspend fun saveLocal(report: VisitReport): Result<Unit> {
        return try {
            visitReportDao.upsert(report.toEntity())
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
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

        // Strapi 5: upload media first, then create entry with file id (no files.* on create).
        val photoPart = MultipartBody.Part.createFormData(
            name = "files",
            filename = photoFile.name,
            body = photoFile.asRequestBody(JPEG_MEDIA_TYPE),
        )

        return try {
            val uploaded = api.uploadFile(files = photoPart)
            val photoId = uploaded.firstOrNull()?.id
                ?: return Result.failure(IllegalStateException("Upload foto tidak mengembalikan id."))

            val response = api.createVisitReport(
                body = VisitReportCreateRequest(
                    data = VisitReportCreateData(
                        store = report.storeId,
                        latitude = report.latitude,
                        longitude = report.longitude,
                        orderNotes = report.orderNotes,
                        photo = photoId,
                    ),
                ),
            )
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(HttpException(response))
            }
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    override suspend fun syncFromRemote(): Result<Unit> {
        return try {
            val entities = api.getVisitReports().data.mapNotNull { it.toEntityOrNull() }
            appDatabase.withTransaction {
                visitReportDao.clearAll()
                if (entities.isNotEmpty()) {
                    visitReportDao.upsertAll(entities)
                }
            }
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private fun VisitReportDto.toEntityOrNull(): VisitReportEntity? {
        val storeId = store?.documentId ?: return null
        return VisitReportEntity(
            id = documentId,
            storeId = storeId,
            latitude = latitude,
            longitude = longitude,
            orderNotes = orderNotes,
            localImagePath = resolveMediaUrl(photo?.url).orEmpty(),
            syncStatus = SyncStatus.SYNCED,
            createdAt = parseStrapiCreatedAt(createdAt),
        )
    }

    private fun resolveMediaUrl(path: String?): String? {
        if (path.isNullOrBlank()) return null
        if (path.startsWith("http://", ignoreCase = true) ||
            path.startsWith("https://", ignoreCase = true)
        ) {
            return path
        }
        return baseUrl.trimEnd('/') + "/" + path.trimStart('/')
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

    private fun parseStrapiCreatedAt(value: String?): Long {
        if (value.isNullOrBlank()) return System.currentTimeMillis()
        return runCatching { STRAPI_CREATED_AT_MS.parse(value)?.time }.getOrNull()
            ?: runCatching { STRAPI_CREATED_AT_S.parse(value)?.time }.getOrNull()
            ?: System.currentTimeMillis()
    }

    private companion object {
        val JPEG_MEDIA_TYPE = "image/jpeg".toMediaType()

        val STRAPI_CREATED_AT_MS = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.US,
        ).apply { timeZone = TimeZone.getTimeZone("UTC") }

        val STRAPI_CREATED_AT_S = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            Locale.US,
        ).apply { timeZone = TimeZone.getTimeZone("UTC") }
    }
}
