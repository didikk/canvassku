package com.didik.canvassku.presentation.screens.home

import com.didik.canvassku.domain.model.StoreListItem
import com.didik.canvassku.domain.model.StoreVisitStatus

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
    val isSyncing: Boolean = false,
    val isSendingPending: Boolean = false,
    val errorMessage: String? = null,
) {
    val filteredStores: List<StoreListItem>
        get() = when (filter) {
            StoreFilter.ALL -> stores
            StoreFilter.NOT_VISITED -> stores.filter { it.status == StoreVisitStatus.NOT_VISITED }
            StoreFilter.PENDING_SYNC -> stores.filter { it.status == StoreVisitStatus.PENDING_SYNC }
            StoreFilter.SYNCED -> stores.filter { it.status == StoreVisitStatus.SYNCED }
        }

    val allCount: Int get() = stores.size
    val notVisitedCount: Int get() = stores.count { it.status == StoreVisitStatus.NOT_VISITED }
    val pendingSyncCount: Int get() = stores.count { it.status == StoreVisitStatus.PENDING_SYNC }
    val syncedCount: Int get() = stores.count { it.status == StoreVisitStatus.SYNCED }
}

sealed interface HomeEffect {
    data object LoggedOut : HomeEffect
    data class ShowMessage(val message: String) : HomeEffect
    data class OpenVisitForm(val storeId: String) : HomeEffect
    data class OpenVisitDetail(val reportId: String) : HomeEffect
}
