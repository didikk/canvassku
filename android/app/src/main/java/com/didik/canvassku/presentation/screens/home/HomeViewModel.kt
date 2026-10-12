package com.didik.canvassku.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.didik.canvassku.domain.model.StoreListItem
import com.didik.canvassku.domain.model.StoreVisitStatus
import com.didik.canvassku.domain.repository.AuthRepository
import com.didik.canvassku.domain.repository.StoreRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import java.io.IOException

@KoinViewModel
class HomeViewModel(
    private val storeRepository: StoreRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect: Flow<HomeEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            storeRepository.observeStoreListItems().collect { stores ->
                _uiState.update { it.copy(stores = stores) }
            }
        }
        viewModelScope.launch {
            storeRepository.observePendingReportCount().collect { count ->
                _uiState.update { it.copy(pendingReportCount = count) }
            }
        }
    }

    fun onFilterSelected(filter: StoreFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun onSyncClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null) }
            storeRepository.syncStores()
                .onSuccess {
                    _uiState.update { it.copy(isSyncing = false) }
                    _effect.send(HomeEffect.ShowMessage("Data toko diperbarui."))
                }
                .onFailure { error ->
                    val message = when (error) {
                        is IOException -> "Sinkronisasi gagal. Periksa jaringan, data lokal tetap ditampilkan."
                        else -> "Sinkronisasi gagal. Coba lagi."
                    }
                    _uiState.update { it.copy(isSyncing = false, errorMessage = message) }
                    _effect.send(HomeEffect.ShowMessage(message))
                }
        }
    }

    fun onSendPendingClick() {
        viewModelScope.launch {
            val pending = _uiState.value.pendingReportCount
            if (pending <= 0) {
                _effect.send(HomeEffect.ShowMessage("Tidak ada laporan tertunda."))
                return@launch
            }
            // Report upload worker lands in Phase 4; keep the CTA honest until then.
            _effect.send(
                HomeEffect.ShowMessage(
                    "Ada $pending laporan tertunda. Pengiriman otomatis aktif setelah form kunjungan.",
                ),
            )
        }
    }

    fun onStoreActionClick(item: StoreListItem) {
        viewModelScope.launch {
            when (item.status) {
                StoreVisitStatus.NOT_VISITED -> {
                    _effect.send(HomeEffect.OpenVisitForm(item.store.id))
                }
                StoreVisitStatus.PENDING_SYNC,
                StoreVisitStatus.SYNCED,
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
        viewModelScope.launch {
            authRepository.logout()
            _effect.send(HomeEffect.LoggedOut)
        }
    }
}
