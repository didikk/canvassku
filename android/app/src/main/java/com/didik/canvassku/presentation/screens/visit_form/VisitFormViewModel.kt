package com.didik.canvassku.presentation.screens.visit_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.didik.canvassku.core.util.ImageCompressor
import com.didik.canvassku.core.util.LocationHelper
import com.didik.canvassku.domain.repository.StoreRepository
import com.didik.canvassku.domain.usecase.SaveVisitReportUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel
import java.io.File

@KoinViewModel
class VisitFormViewModel(
    @InjectedParam private val storeId: String,
    private val storeRepository: StoreRepository,
    private val locationHelper: LocationHelper,
    private val imageCompressor: ImageCompressor,
    private val saveVisitReportUseCase: SaveVisitReportUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VisitFormState())
    val uiState: StateFlow<VisitFormState> = _uiState.asStateFlow()

    private val _effect = Channel<VisitFormEffect>(Channel.BUFFERED)
    val effect: Flow<VisitFormEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            storeRepository.observeStore(storeId).collect { store ->
                _uiState.update { it.copy(store = store) }
            }
        }
        viewModelScope.launch {
            _effect.send(VisitFormEffect.RequestLocationPermission)
        }
    }

    fun onOrderNotesChange(value: String) {
        _uiState.update { state ->
            state.copy(
                orderNotes = value,
                status = if (state.status is VisitFormState.Status.Error) {
                    VisitFormState.Status.Idle
                } else {
                    state.status
                },
            )
        }
    }

    fun onLocationPermissionResult(granted: Boolean) {
        if (!granted) {
            _uiState.update {
                it.copy(location = VisitFormState.LocationUi.PermissionRequired)
            }
            return
        }
        lockLocation()
    }

    fun onRetryLocationClick() {
        viewModelScope.launch {
            _effect.send(VisitFormEffect.RequestLocationPermission)
        }
    }

    fun onPhotoCaptured(tempFilePath: String) {
        viewModelScope.launch {
            runCatching {
                imageCompressor.compressToFilesDir(File(tempFilePath))
            }.onSuccess { compressed ->
                _uiState.update {
                    it.copy(
                        localImagePath = compressed.absolutePath,
                        status = if (it.status is VisitFormState.Status.Error) {
                            VisitFormState.Status.Idle
                        } else {
                            it.status
                        },
                    )
                }
            }.onFailure {
                _effect.send(VisitFormEffect.ShowMessage("Gagal memproses foto. Coba ambil ulang."))
            }
            withContext(Dispatchers.IO) {
                File(tempFilePath).delete()
            }
        }
    }

    fun onSubmitClick() {
        val state = _uiState.value
        val location = state.location as? VisitFormState.LocationUi.Ready
        if (location == null) {
            _uiState.update {
                it.copy(status = VisitFormState.Status.Error("Lokasi belum terkunci. Izinkan GPS terlebih dahulu."))
            }
            return
        }
        val imagePath = state.localImagePath
        if (imagePath.isNullOrBlank()) {
            _uiState.update {
                it.copy(status = VisitFormState.Status.Error("Foto bukti kunjungan wajib diambil."))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(status = VisitFormState.Status.Saving) }
            saveVisitReportUseCase(
                storeId = storeId,
                latitude = location.latitude,
                longitude = location.longitude,
                orderNotes = state.orderNotes,
                localImagePath = imagePath,
            ).onSuccess {
                _effect.send(VisitFormEffect.ShowMessage("Laporan tersimpan lokal. Menunggu sinkronisasi."))
                _effect.send(VisitFormEffect.NavigateBack)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        status = VisitFormState.Status.Error(
                            error.message?.takeIf { msg -> msg.isNotBlank() }
                                ?: "Gagal menyimpan laporan.",
                        ),
                    )
                }
            }
        }
    }

    private fun lockLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(location = VisitFormState.LocationUi.Loading) }
            locationHelper.lockCurrentLocation()
                .onSuccess { point ->
                    _uiState.update {
                        it.copy(
                            location = VisitFormState.LocationUi.Ready(
                                latitude = point.latitude,
                                longitude = point.longitude,
                            ),
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            location = VisitFormState.LocationUi.Error(
                                "Lokasi gagal dibaca. Aktifkan GPS lalu coba lagi.",
                            ),
                        )
                    }
                }
        }
    }
}
