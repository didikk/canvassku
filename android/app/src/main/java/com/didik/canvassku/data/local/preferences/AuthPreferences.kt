package com.didik.canvassku.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

internal fun authPreferencesDataStore(context: Context): DataStore<Preferences> =
    context.applicationContext.authDataStore

class AuthPreferences(
    private val dataStore: DataStore<Preferences>,
) {
    @Volatile
    private var cachedJwtToken: String? = null

    fun peekJwtToken(): String? = cachedJwtToken

    suspend fun currentJwtToken(): String? {
        cachedJwtToken?.let { return it }
        return dataStore.data.map { it[KEY_JWT_TOKEN] }.first().also { cachedJwtToken = it }
    }

    suspend fun currentRefreshToken(): String? =
        dataStore.data.map { it[KEY_REFRESH_TOKEN] }.first()

    suspend fun saveSession(jwtToken: String, refreshToken: String?) {
        dataStore.edit { prefs ->
            prefs[KEY_JWT_TOKEN] = jwtToken
            if (refreshToken != null) {
                prefs[KEY_REFRESH_TOKEN] = refreshToken
            } else {
                prefs.remove(KEY_REFRESH_TOKEN)
            }
        }
        cachedJwtToken = jwtToken
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_JWT_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            // Keep KEY_SKIP_VISIT_SYNC_ON_LOGIN — survives ForceReLogin process death.
        }
        cachedJwtToken = null
    }

    suspend fun markSkipVisitSyncOnNextLogin() {
        dataStore.edit { prefs ->
            prefs[KEY_SKIP_VISIT_SYNC_ON_LOGIN] = true
        }
    }

    suspend fun isSkipVisitSyncOnNextLogin(): Boolean =
        dataStore.data.map { it[KEY_SKIP_VISIT_SYNC_ON_LOGIN] == true }.first()

    suspend fun clearSkipVisitSyncOnNextLogin() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_SKIP_VISIT_SYNC_ON_LOGIN)
        }
    }

    private companion object {
        val KEY_JWT_TOKEN = stringPreferencesKey("jwt_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_SKIP_VISIT_SYNC_ON_LOGIN = booleanPreferencesKey("skip_visit_sync_on_login")
    }
}
