package com.didik.canvassku.domain.repository

import com.didik.canvassku.domain.model.Store
import com.didik.canvassku.domain.model.StoreListItem
import kotlinx.coroutines.flow.Flow

interface StoreRepository {
    fun observeStores(): Flow<List<Store>>
    fun observeStoreListItems(): Flow<List<StoreListItem>>
    fun observePendingReportCount(): Flow<Int>
    suspend fun syncStores(): Result<Unit>
}
