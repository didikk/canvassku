package com.didik.canvassku.domain.model

data class AuthSession(
    val jwtToken: String,
    val refreshToken: String?,
    val tokenTimestamp: Long,
)
