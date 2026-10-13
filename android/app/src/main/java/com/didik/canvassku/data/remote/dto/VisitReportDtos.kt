package com.didik.canvassku.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitReportCreateData(
    val store: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("order_notes") val orderNotes: String,
)
