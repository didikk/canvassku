package com.didik.canvassku.presentation.screens.visit_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.didik.canvassku.domain.repository.StoreRepository
import com.didik.canvassku.domain.repository.VisitRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.KoinViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@KoinViewModel
class VisitDetailViewModel(
    @InjectedParam private val reportId: String,
    private val visitRepository: VisitRepository,
    private val storeRepository: StoreRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<VisitDetailState>(VisitDetailState.Loading)
    val uiState: StateFlow<VisitDetailState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            visitRepository.observeById(reportId)
                .flatMapLatest { report ->
                    if (report == null) {
                        flowOf(VisitDetailState.NotFound)
                    } else {
                        storeRepository.observeStore(report.storeId).map { store ->
                            VisitDetailState.Ready(report = report, store = store)
                        }
                    }
                }
                .collect { state ->
                    _uiState.update { state }
                }
        }
    }
}
