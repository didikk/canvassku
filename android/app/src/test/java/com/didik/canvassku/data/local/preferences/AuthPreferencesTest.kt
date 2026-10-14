package com.didik.canvassku.data.local.preferences

import com.didik.canvassku.testing.createInMemoryAuthPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class AuthPreferencesTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun saveSession_persistsJwtAndRefreshToken() = runTest {
        val prefs = createInMemoryAuthPreferences(temporaryFolder)

        prefs.saveSession(jwtToken = "jwt-1", refreshToken = "refresh-1")

        assertEquals("jwt-1", prefs.currentJwtToken())
        assertEquals("jwt-1", prefs.peekJwtToken())
        assertEquals("refresh-1", prefs.currentRefreshToken())
    }

    @Test
    fun clearSession_removesTokens_keepsSkipVisitSyncFlag() = runTest {
        val prefs = createInMemoryAuthPreferences(temporaryFolder)

        prefs.saveSession(jwtToken = "jwt-1", refreshToken = "refresh-1")
        prefs.markSkipVisitSyncOnNextLogin()
        prefs.clearSession()

        assertNull(prefs.currentJwtToken())
        assertNull(prefs.peekJwtToken())
        assertNull(prefs.currentRefreshToken())
        assertTrue(prefs.isSkipVisitSyncOnNextLogin())
    }
}
