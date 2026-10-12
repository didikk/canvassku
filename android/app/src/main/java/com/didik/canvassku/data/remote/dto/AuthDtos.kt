package com.didik.canvassku.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val identifier: String,
    val password: String,
)

@Serializable
data class LoginResponseDto(
    val jwt: String,
    val refreshToken: String? = null,
    val user: StrapiUserDto? = null,
)

@Serializable
data class RefreshRequestDto(
    val refreshToken: String,
)

@Serializable
data class RefreshResponseDto(
    val jwt: String,
    val refreshToken: String? = null,
)

@Serializable
data class StrapiUserDto(
    val id: Long? = null,
    val username: String? = null,
    val email: String? = null,
)
