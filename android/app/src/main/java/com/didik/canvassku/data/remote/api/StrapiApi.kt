package com.didik.canvassku.data.remote.api

import com.didik.canvassku.data.remote.dto.LoginRequestDto
import com.didik.canvassku.data.remote.dto.LoginResponseDto
import com.didik.canvassku.data.remote.dto.StoreDto
import com.didik.canvassku.data.remote.dto.StrapiListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StrapiApi {
    @POST("api/auth/local")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @GET("api/stores")
    suspend fun getStores(): StrapiListResponse<StoreDto>
}
