package com.didik.canvassku.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StrapiListResponse<T>(
    val data: List<T> = emptyList(),
)

@Serializable
data class StoreDto(
    val id: Long? = null,
    val documentId: String,
    val name: String,
    val address: String? = null,
    @SerialName("owner_name") val ownerName: String? = null,
)
