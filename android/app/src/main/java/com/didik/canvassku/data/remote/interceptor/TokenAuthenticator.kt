package com.didik.canvassku.data.remote.interceptor

import com.didik.canvassku.data.local.preferences.AuthPreferences
import com.didik.canvassku.data.remote.AuthEventBus
import com.didik.canvassku.data.remote.dto.RefreshRequestDto
import com.didik.canvassku.data.remote.dto.RefreshResponseDto
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import org.koin.core.annotation.Named
import org.koin.core.annotation.Singleton
import java.io.IOException

@Singleton
class TokenAuthenticator(
    private val authPreferences: AuthPreferences,
    private val authEventBus: AuthEventBus,
    private val json: Json,
    private val baseUrl: String,
    @Named("refresh") private val refreshClient: OkHttpClient,
) : Authenticator {
    private val refreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.isAuthExempt()) return null
        if (responseCount(response) >= 2) return null

        return runBlocking {
            refreshMutex.withLock {
                val latestJwt = authPreferences.currentJwtToken()
                val requestToken = response.request.header(AuthInterceptor.HEADER_AUTHORIZATION)
                    ?.removePrefix("Bearer ")
                    ?.trim()

                // Another call may have refreshed already.
                if (!latestJwt.isNullOrBlank() && latestJwt != requestToken) {
                    return@withLock response.request.newBuilder()
                        .header(AuthInterceptor.HEADER_AUTHORIZATION, "Bearer $latestJwt")
                        .build()
                }

                val refreshToken = authPreferences.currentRefreshToken()
                if (refreshToken.isNullOrBlank()) {
                    forceReLogin()
                    return@withLock null
                }

                when (val outcome = refreshAccessToken(refreshToken)) {
                    is RefreshOutcome.Success -> {
                        authPreferences.saveSession(
                            jwtToken = outcome.body.jwt,
                            refreshToken = outcome.body.refreshToken ?: refreshToken,
                        )
                        response.request.newBuilder()
                            .header(
                                AuthInterceptor.HEADER_AUTHORIZATION,
                                "Bearer ${outcome.body.jwt}",
                            )
                            .build()
                    }
                    RefreshOutcome.Unauthorized -> {
                        forceReLogin()
                        null
                    }
                    RefreshOutcome.TransientFailure -> {
                        // Keep local session for offline-first; let the call fail once.
                        null
                    }
                }
            }
        }
    }

    private fun refreshAccessToken(refreshToken: String): RefreshOutcome {
        val body = json.encodeToString(RefreshRequestDto.serializer(), RefreshRequestDto(refreshToken))
            .toRequestBody(JSON_MEDIA_TYPE)
        val request = Request.Builder()
            .url(baseUrl.trimEnd('/') + "/api/auth/refresh")
            .post(body)
            .header("Content-Type", "application/json")
            .build()

        return try {
            refreshClient.newCall(request).execute().use { refreshResponse ->
                when {
                    refreshResponse.isSuccessful -> {
                        val payload = refreshResponse.body.string()
                        val dto = json.decodeFromString(RefreshResponseDto.serializer(), payload)
                        RefreshOutcome.Success(dto)
                    }
                    refreshResponse.code == 401 || refreshResponse.code == 403 -> {
                        RefreshOutcome.Unauthorized
                    }
                    else -> RefreshOutcome.TransientFailure
                }
            }
        } catch (_: Exception) {
            RefreshOutcome.TransientFailure
        }
    }

    private suspend fun forceReLogin() {
        authPreferences.markSkipVisitSyncOnNextLogin()
        authPreferences.clearSession()
        authEventBus.emitSessionExpired()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private sealed interface RefreshOutcome {
        data class Success(val body: RefreshResponseDto) : RefreshOutcome
        data object Unauthorized : RefreshOutcome
        data object TransientFailure : RefreshOutcome
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
