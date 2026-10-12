package com.didik.canvassku.data.remote.api

import com.didik.canvassku.data.remote.dto.LoginRequestDto
import com.didik.canvassku.data.remote.dto.LoginResponseDto
import com.didik.canvassku.data.remote.dto.RefreshRequestDto
import com.didik.canvassku.data.remote.dto.RefreshResponseDto
import com.didik.canvassku.data.remote.dto.StoreDto
import com.didik.canvassku.data.remote.dto.StrapiListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StrapiApi {
    @POST("api/auth/local")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    // Matches backend users-permissions refresh action (POST /api/auth/refresh).
    @POST("api/auth/refresh")
    suspend fun refreshToken(@Body body: RefreshRequestDto): RefreshResponseDto

    @GET("api/stores")
    suspend fun getStores(): StrapiListResponse<StoreDto>
}
