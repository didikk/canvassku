package com.didik.canvassku.presentation.screens.visit_form

import com.didik.canvassku.domain.model.Store

data class VisitFormState(
    val store: Store? = null,
    val orderNotes: String = "",
    val localImagePath: String? = null,
    val location: LocationUi = LocationUi.Loading,
    val status: Status = Status.Idle,
) {
    val canSubmit: Boolean
        get() = status !is Status.Saving &&
            location is LocationUi.Ready &&
            !localImagePath.isNullOrBlank() &&
            orderNotes.trim().isNotEmpty()

    sealed interface LocationUi {
        data object Loading : LocationUi
        data object PermissionRequired : LocationUi
        data class Ready(val latitude: Double, val longitude: Double) : LocationUi
        data class Error(val message: String) : LocationUi
    }

    sealed interface Status {
        data object Idle : Status
        data object Saving : Status
        data class Error(val message: String) : Status
    }
}

sealed interface VisitFormEffect {
    data object NavigateBack : VisitFormEffect
    data object RequestLocationPermission : VisitFormEffect
    data class ShowMessage(val message: String) : VisitFormEffect
}
