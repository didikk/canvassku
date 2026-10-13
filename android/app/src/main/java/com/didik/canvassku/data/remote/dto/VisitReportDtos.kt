package com.didik.canvassku.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VisitReportCreateRequest(
    val data: VisitReportCreateData,
)

@Serializable
data class VisitReportCreateData(
    val store: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("order_notes") val orderNotes: String,
    val photo: Long,
)

@Serializable
data class VisitReportDto(
    val documentId: String,
    val latitude: Double,
    val longitude: Double,
    @SerialName("order_notes") val orderNotes: String,
    val createdAt: String? = null,
    val store: VisitReportStoreRef? = null,
    val photo: StrapiMediaDto? = null,
)

@Serializable
data class VisitReportStoreRef(
    val documentId: String,
)

@Serializable
data class StrapiMediaDto(
    val url: String? = null,
)

@Serializable
data class UploadedFileDto(
    val id: Long,
    val documentId: String? = null,
    val url: String? = null,
)
