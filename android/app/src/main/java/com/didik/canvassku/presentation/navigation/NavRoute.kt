package com.didik.canvassku.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoute : NavKey {
    @Serializable
    data object Login : NavRoute

    @Serializable
    data object Home : NavRoute

    @Serializable
    data class VisitForm(val storeId: String) : NavRoute

    @Serializable
    data class VisitDetail(val reportId: String) : NavRoute
}
