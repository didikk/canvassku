package com.didik.canvassku.data.remote.api

import com.didik.canvassku.data.remote.dto.LoginRequestDto
import com.didik.canvassku.data.remote.dto.LoginResponseDto
import com.didik.canvassku.data.remote.dto.StoreDto
import com.didik.canvassku.data.remote.dto.StrapiListResponse
import com.didik.canvassku.data.remote.dto.UploadedFileDto
import com.didik.canvassku.data.remote.dto.VisitReportCreateRequest
import com.didik.canvassku.data.remote.dto.VisitReportDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface StrapiApi {
    @POST("api/auth/local")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @GET("api/stores")
    suspend fun getStores(): StrapiListResponse<StoreDto>

    @GET("api/visit-reports")
    suspend fun getVisitReports(
        @Query("populate") populate: String = "*",
    ): StrapiListResponse<VisitReportDto>

    @Multipart
    @POST("api/upload")
    suspend fun uploadFile(
        @Part files: MultipartBody.Part,
    ): List<UploadedFileDto>

    @POST("api/visit-reports")
    suspend fun createVisitReport(
        @Body body: VisitReportCreateRequest,
    ): Response<Unit>
}
