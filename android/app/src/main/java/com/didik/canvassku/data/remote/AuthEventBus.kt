package com.didik.canvassku.data.remote

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.annotation.Singleton

@Singleton
class AuthEventBus {
    private val _sessionExpired = Channel<Unit>(Channel.BUFFERED)
    val sessionExpired: Flow<Unit> = _sessionExpired.receiveAsFlow()

    suspend fun emitSessionExpired() {
        _sessionExpired.send(Unit)
    }
}
