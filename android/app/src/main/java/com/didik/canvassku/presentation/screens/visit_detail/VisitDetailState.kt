package com.didik.canvassku.presentation.screens.visit_detail

import com.didik.canvassku.domain.model.Store
import com.didik.canvassku.domain.model.VisitReport

sealed class VisitDetailState {
    data object Loading : VisitDetailState()
    data class Ready(
        val report: VisitReport,
        val store: Store?,
    ) : VisitDetailState()
    data object NotFound : VisitDetailState()
}
