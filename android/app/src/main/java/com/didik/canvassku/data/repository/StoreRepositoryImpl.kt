package com.didik.canvassku.data.repository

import androidx.room.withTransaction
import com.didik.canvassku.data.local.db.AppDatabase
import com.didik.canvassku.data.local.db.StoreDao
import com.didik.canvassku.data.local.db.VisitReportDao
import com.didik.canvassku.data.local.entity.StoreEntity
import com.didik.canvassku.data.local.entity.SyncStatus
import com.didik.canvassku.data.local.entity.VisitReportEntity
import com.didik.canvassku.data.remote.api.StrapiApi
import com.didik.canvassku.domain.model.Store
import com.didik.canvassku.domain.model.StoreListItem
import com.didik.canvassku.domain.model.StoreVisitStatus
import com.didik.canvassku.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.annotation.Singleton

@Singleton
class StoreRepositoryImpl(
    private val api: StrapiApi,
    private val appDatabase: AppDatabase,
    private val storeDao: StoreDao,
    private val visitReportDao: VisitReportDao,
) : StoreRepository {
    override fun observeStoreListItems(): Flow<List<StoreListItem>> {
        return combine(
            storeDao.observeAll(),
            visitReportDao.observeAll(),
        ) { stores, reports ->
            val latestByStore = reports
                .groupBy { it.storeId }
                .mapValues { (_, items) -> items.maxByOrNull { it.createdAt } }

            stores.map { entity ->
                val report = latestByStore[entity.id]
                StoreListItem(
                    store = entity.toDomain(),
                    status = report.toVisitStatus(),
                    latestReportId = report?.id,
                    latestReportCreatedAt = report?.createdAt,
                )
            }
        }
    }

    override fun observePendingReportCount(): Flow<Int> = visitReportDao.observePendingCount()

    override suspend fun syncStores(): Result<Unit> {
        return try {
            val entities = api.getStores().data.map { dto ->
                StoreEntity(
                    id = dto.documentId,
                    name = dto.name,
                    address = dto.address,
                    ownerName = dto.ownerName,
                )
            }
            if (entities.isEmpty()) {
                return Result.failure(IllegalStateException("Server mengembalikan daftar toko kosong."))
            }
            appDatabase.withTransaction {
                storeDao.clearAll()
                storeDao.upsertAll(entities)
            }
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private fun VisitReportEntity?.toVisitStatus(): StoreVisitStatus {
        return when {
            this == null -> StoreVisitStatus.NOT_VISITED
            syncStatus == SyncStatus.SYNCED -> StoreVisitStatus.SYNCED
            else -> StoreVisitStatus.PENDING_SYNC
        }
    }

    private fun StoreEntity.toDomain(): Store = Store(
        id = id,
        name = name,
        address = address,
        ownerName = ownerName,
    )
}
