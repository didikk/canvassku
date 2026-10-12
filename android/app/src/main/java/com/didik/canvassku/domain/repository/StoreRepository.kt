package com.didik.canvassku.domain.repository

import com.didik.canvassku.domain.model.StoreListItem
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun observeStoreListItems(): Flow<List<StoreListItem>>
    fun observePendingReportCount(): Flow<Int>
    suspend fun syncStores(): Result<Unit>
}
