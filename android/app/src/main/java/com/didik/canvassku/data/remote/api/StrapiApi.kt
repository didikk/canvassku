package com.didik.canvassku.data.remote.api

import com.didik.canvassku.data.remote.dto.LoginRequestDto
import com.didik.canvassku.data.remote.dto.LoginResponseDto
import com.didik.canvassku.data.remote.dto.StoreDto
import com.didik.canvassku.data.remote.dto.StrapiListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface StrapiApi {
    @POST("api/auth/local")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @GET("api/stores")
    suspend fun getStores(): StrapiListResponse<StoreDto>

    @Multipart
    @POST("api/visit-reports")
    suspend fun createVisitReport(
        @Part("data") data: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Response<Unit>
}
