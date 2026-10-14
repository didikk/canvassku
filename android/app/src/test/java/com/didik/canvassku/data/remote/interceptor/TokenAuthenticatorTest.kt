package com.didik.canvassku.data.remote.interceptor

import com.didik.canvassku.data.remote.AuthEventBus
import com.didik.canvassku.testing.createInMemoryAuthPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class TokenAuthenticatorTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var server: MockWebServer
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun authenticate_on401_refreshesToken_andRetriesRequest() = runTest {
        val prefs = createInMemoryAuthPreferences(temporaryFolder)
        prefs.saveSession(jwtToken = "old-jwt", refreshToken = "refresh-1")
        val authEventBus = AuthEventBus()

        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return when {
                    request.path?.contains("/api/auth/refresh") == true -> {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody("""{"jwt":"new-jwt","refreshToken":"refresh-2"}""")
                    }
                    request.getHeader("Authorization") == "Bearer new-jwt" -> {
                        MockResponse().setResponseCode(200).setBody("""{"ok":true}""")
                    }
                    else -> MockResponse().setResponseCode(401).setBody("""{"error":"expired"}""")
                }
            }
        }

        val refreshClient = OkHttpClient()
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefs))
            .authenticator(
                TokenAuthenticator(
                    authPreferences = prefs,
                    authEventBus = authEventBus,
                    json = json,
                    baseUrl = server.url("/").toString(),
                    refreshClient = refreshClient,
                ),
            )
            .build()

        val response = client.newCall(
            Request.Builder().url(server.url("/api/stores")).get().build(),
        ).execute()

        assertEquals(200, response.code)
        response.close()
        assertEquals("new-jwt", prefs.currentJwtToken())
        assertEquals("refresh-2", prefs.currentRefreshToken())
    }

    @Test
    fun authenticate_onRefreshUnauthorized_clearsSession_andEmitsExpired() = runTest {
        val prefs = createInMemoryAuthPreferences(temporaryFolder)
        prefs.saveSession(jwtToken = "old-jwt", refreshToken = "refresh-1")
        val authEventBus = AuthEventBus()

        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setResponseCode(401))

        val expiredLatch = CountDownLatch(1)
        thread {
            runBlocking(Dispatchers.IO) {
                withTimeout(3_000) { authEventBus.sessionExpired.first() }
                expiredLatch.countDown()
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefs))
            .authenticator(
                TokenAuthenticator(
                    authPreferences = prefs,
                    authEventBus = authEventBus,
                    json = json,
                    baseUrl = server.url("/").toString(),
                    refreshClient = OkHttpClient(),
                ),
            )
            .build()

        val response = client.newCall(
            Request.Builder().url(server.url("/api/stores")).get().build(),
        ).execute()

        assertEquals(401, response.code)
        response.close()
        assertTrue(expiredLatch.await(3, TimeUnit.SECONDS))
        assertNull(prefs.currentJwtToken())
        assertNull(prefs.currentRefreshToken())
    }
}
