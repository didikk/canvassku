package com.didik.canvassku.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class AuthPreferences(context: Context) {
    private val dataStore = context.applicationContext.authDataStore

    @Volatile
    private var cachedJwtToken: String? = null

    val jwtToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_JWT_TOKEN].also { cachedJwtToken = it }
    }

    val refreshToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_REFRESH_TOKEN]
    }

    val tokenTimestamp: Flow<Long?> = dataStore.data.map { prefs ->
        prefs[KEY_TOKEN_TIMESTAMP]
    }

    val session: Flow<AuthSessionPrefs?> = dataStore.data.map { prefs ->
        val jwt = prefs[KEY_JWT_TOKEN] ?: return@map null
        cachedJwtToken = jwt
        AuthSessionPrefs(
            jwtToken = jwt,
            refreshToken = prefs[KEY_REFRESH_TOKEN],
            tokenTimestamp = prefs[KEY_TOKEN_TIMESTAMP] ?: 0L,
        )
    }

    fun peekJwtToken(): String? = cachedJwtToken

    suspend fun currentJwtToken(): String? {
        cachedJwtToken?.let { return it }
        return jwtToken.first().also { cachedJwtToken = it }
    }

    suspend fun currentRefreshToken(): String? = refreshToken.first()

    suspend fun saveSession(jwtToken: String, refreshToken: String?, tokenTimestamp: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_JWT_TOKEN] = jwtToken
            if (refreshToken != null) {
                prefs[KEY_REFRESH_TOKEN] = refreshToken
            } else {
                prefs.remove(KEY_REFRESH_TOKEN)
            }
            prefs[KEY_TOKEN_TIMESTAMP] = tokenTimestamp
        }
        cachedJwtToken = jwtToken
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_JWT_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
            prefs.remove(KEY_TOKEN_TIMESTAMP)
        }
        cachedJwtToken = null
    }

    private companion object {
        val KEY_JWT_TOKEN = stringPreferencesKey("jwt_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_TOKEN_TIMESTAMP = longPreferencesKey("token_timestamp")
    }
}

data class AuthSessionPrefs(
    val jwtToken: String,
    val refreshToken: String?,
    val tokenTimestamp: Long,
)
