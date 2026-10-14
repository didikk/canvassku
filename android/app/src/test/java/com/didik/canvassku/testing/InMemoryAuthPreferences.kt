package com.didik.canvassku.testing

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.didik.canvassku.data.local.preferences.AuthPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.junit.rules.TemporaryFolder
import java.util.UUID

fun createInMemoryAuthPreferences(
    temporaryFolder: TemporaryFolder,
): AuthPreferences {
    // Real dispatcher — OkHttp Authenticator/Interceptor use runBlocking off the test thread.
    val dataStore = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
        produceFile = {
            temporaryFolder.root.resolve("auth-${UUID.randomUUID()}.preferences_pb")
        },
    )
    return AuthPreferences(dataStore)
}
