package com.didik.canvassku.presentation.screens.home

import com.didik.canvassku.domain.model.StoreListItem
import com.didik.canvassku.domain.model.SyncStatus

enum class StoreFilter {
    ALL,
    NOT_VISITED,
    PENDING_SYNC,
    SYNCED,
}

data class HomeState(
    val stores: List<StoreListItem> = emptyList(),
    val filter: StoreFilter = StoreFilter.ALL,
    val pendingReportCount: Int = 0,
    val isStoreSyncing: Boolean = false,
    val isReportSyncing: Boolean = false,
    val showLogoutConfirm: Boolean = false,
) {
    val isSyncing: Boolean get() = isStoreSyncing || isReportSyncing

    val filteredStores: List<StoreListItem>
        get() = when (filter) {
            StoreFilter.ALL -> stores
            StoreFilter.NOT_VISITED -> stores.filter { it.status == SyncStatus.NOT_VISITED }
            StoreFilter.PENDING_SYNC -> stores.filter { it.status == SyncStatus.PENDING_SYNC }
            StoreFilter.SYNCED -> stores.filter { it.status == SyncStatus.SYNCED }
        }

    val allCount: Int get() = stores.size
    val notVisitedCount: Int get() = stores.count { it.status == SyncStatus.NOT_VISITED }
    val pendingSyncCount: Int get() = stores.count { it.status == SyncStatus.PENDING_SYNC }
    val syncedCount: Int get() = stores.count { it.status == SyncStatus.SYNCED }
}

sealed interface HomeEffect {
    data object LoggedOut : HomeEffect
    data class ShowMessage(val message: String) : HomeEffect
    data class OpenVisitForm(val storeId: String) : HomeEffect
    data class OpenVisitDetail(val reportId: String) : HomeEffect
}
