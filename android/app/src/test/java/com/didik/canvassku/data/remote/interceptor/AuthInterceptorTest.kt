package com.didik.canvassku.data.remote.interceptor

import com.didik.canvassku.testing.createInMemoryAuthPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class AuthInterceptorTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    private lateinit var server: MockWebServer

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
    fun intercept_attachesBearerToken_forProtectedRoutes() = runTest {
        val prefs = createInMemoryAuthPreferences(temporaryFolder)
        prefs.saveSession(jwtToken = "access-token", refreshToken = "refresh-token")
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefs))
            .build()

        client.newCall(
            Request.Builder().url(server.url("/api/stores")).get().build(),
        ).execute().close()

        assertEquals("Bearer access-token", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun intercept_skipsAuthHeader_forLoginRoute() = runTest {
        val prefs = createInMemoryAuthPreferences(temporaryFolder)
        prefs.saveSession(jwtToken = "access-token", refreshToken = "refresh-token")
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefs))
            .build()

        client.newCall(
            Request.Builder()
                .url(server.url("/api/auth/local"))
                .post(okhttp3.RequestBody.EMPTY)
                .build(),
        ).execute().close()

        assertNull(server.takeRequest().getHeader("Authorization"))
    }
}
