package com.didik.canvassku.data.remote

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.annotation.Singleton

sealed interface AuthEffect {
    data object ForceReLogin : AuthEffect
}

@Singleton
class AuthEventBus {
    private val _effects = Channel<AuthEffect>(Channel.BUFFERED)
    val effects: Flow<AuthEffect> = _effects.receiveAsFlow()

    suspend fun emit(effect: AuthEffect) {
        _effects.send(effect)
    }
}
