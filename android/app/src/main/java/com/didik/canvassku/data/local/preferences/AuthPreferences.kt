package com.didik.canvassku.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class AuthPreferences(context: Context) {
    private val dataStore = context.applicationContext.authDataStore

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
        }
        cachedJwtToken = null
    }

    private companion object {
        val KEY_JWT_TOKEN = stringPreferencesKey("jwt_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}
