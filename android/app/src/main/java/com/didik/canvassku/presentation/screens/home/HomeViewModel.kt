package com.didik.canvassku.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.didik.canvassku.domain.model.StoreListItem
import com.didik.canvassku.domain.model.SyncStatus
import com.didik.canvassku.domain.repository.AuthRepository
import com.didik.canvassku.domain.repository.StoreRepository
import com.didik.canvassku.domain.repository.SyncScheduler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(
    private val storeRepository: StoreRepository,
    private val authRepository: AuthRepository,
    private val syncScheduler: SyncScheduler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect: Flow<HomeEffect> = _effect.receiveAsFlow()

    private var suppressSyncCompleteMessage = false

    init {
        viewModelScope.launch {
            storeRepository.observeStoreListItems().collect { stores ->
                _uiState.update { it.copy(stores = stores) }
            }
        }
        viewModelScope.launch {
            var previousPending = -1
            storeRepository.observePendingReportCount().collect { count ->
                if (previousPending > 0 && count == 0 && !suppressSyncCompleteMessage) {
                    _effect.send(HomeEffect.ShowMessage("Laporan terkirim."))
                }
                suppressSyncCompleteMessage = false
                previousPending = count
                _uiState.update { it.copy(pendingReportCount = count) }
            }
        }
        viewModelScope.launch {
            syncScheduler.observeIsSyncing().collect { syncing ->
                _uiState.update { it.copy(isReportSyncing = syncing) }
            }
        }
    }

    fun onFilterSelected(filter: StoreFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun onSendPendingClick() {
        viewModelScope.launch {
            val pending = _uiState.value.pendingReportCount
            if (pending <= 0) {
                _effect.send(HomeEffect.ShowMessage("Tidak ada laporan tertunda."))
                return@launch
            }
            syncScheduler.enqueuePendingSync()
            _effect.send(
                HomeEffect.ShowMessage(
                    "Mengirim $pending laporan tertunda saat jaringan tersedia.",
                ),
            )
        }
    }

    fun onStoreActionClick(item: StoreListItem) {
        viewModelScope.launch {
            when (item.status) {
                SyncStatus.NOT_VISITED -> {
                    _effect.send(HomeEffect.OpenVisitForm(item.store.id))
                }
                SyncStatus.PENDING_SYNC,
                SyncStatus.SYNCED,
                SyncStatus.FAILED,
                -> {
                    val reportId = item.latestReportId
                    if (reportId != null) {
                        _effect.send(HomeEffect.OpenVisitDetail(reportId))
                    } else {
                        _effect.send(HomeEffect.OpenVisitForm(item.store.id))
                    }
                }
            }
        }
    }

    fun onLogoutClick() {
        _uiState.update { it.copy(showLogoutConfirm = true) }
    }

    fun onLogoutDismiss() {
        _uiState.update { it.copy(showLogoutConfirm = false) }
    }

    fun onLogoutConfirm() {
        viewModelScope.launch {
            suppressSyncCompleteMessage = true
            _uiState.update { it.copy(showLogoutConfirm = false) }
            authRepository.logout()
            _effect.send(HomeEffect.LoggedOut)
        }
    }
}
