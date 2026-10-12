package com.didik.canvassku.core.di

import com.didik.canvassku.BuildConfig
import com.didik.canvassku.data.remote.api.StrapiApi
import com.didik.canvassku.data.remote.interceptor.AuthInterceptor
import com.didik.canvassku.data.remote.interceptor.TokenAuthenticator
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

@Singleton
fun provideJson(): Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
}

@Singleton
fun provideStrapiBaseUrl(): String = BuildConfig.STRAPI_BASE_URL

@Singleton
@Named("refresh")
fun provideRefreshOkHttpClient(): OkHttpClient {
    // No authenticator/interceptor: refresh must not re-enter TokenAuthenticator on 401.
    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
}

@Singleton
fun provideOkHttpClient(
    authInterceptor: AuthInterceptor,
    tokenAuthenticator: TokenAuthenticator,
): OkHttpClient {
    val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
    return OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .authenticator(tokenAuthenticator)
        .build()
}

@Singleton
fun provideRetrofit(
    okHttpClient: OkHttpClient,
    json: Json,
    baseUrl: String,
): Retrofit {
    val contentType = "application/json".toMediaType()
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory(contentType))
        .build()
}

@Singleton
fun provideStrapiApi(retrofit: Retrofit): StrapiApi = retrofit.create(StrapiApi::class.java)
