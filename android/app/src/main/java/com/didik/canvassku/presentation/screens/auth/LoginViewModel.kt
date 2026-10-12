package com.didik.canvassku.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.didik.canvassku.domain.usecase.LoginUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel
import retrofit2.HttpException
import java.io.IOException

@KoinViewModel
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect: Flow<LoginEffect> = _effect.receiveAsFlow()

    fun onIdentifierChange(value: String) {
        _uiState.update { state ->
            state.copy(
                identifier = value,
                status = if (state.status is LoginState.Status.Error) {
                    LoginState.Status.Idle
                } else {
                    state.status
                },
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { state ->
            state.copy(
                password = value,
                status = if (state.status is LoginState.Status.Error) {
                    LoginState.Status.Idle
                } else {
                    state.status
                },
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onLoginClick() {
        val identifier = _uiState.value.identifier.trim()
        val password = _uiState.value.password
        if (identifier.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(status = LoginState.Status.Error("Email dan kata sandi wajib diisi.")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(status = LoginState.Status.Loading) }
            loginUseCase(identifier, password)
                .onSuccess {
                    _uiState.update { it.copy(status = LoginState.Status.Idle) }
                    _effect.send(LoginEffect.NavigateHome)
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(status = LoginState.Status.Error(error.toLoginMessage()))
                    }
                }
        }
    }

    private fun Throwable.toLoginMessage(): String {
        return when (this) {
            is HttpException -> when (code()) {
                400, 401 -> "Email atau kata sandi salah."
                else -> "Login gagal. Coba lagi."
            }
            is IOException -> "Tidak ada koneksi. Periksa jaringan Anda."
            else -> message?.takeIf { it.isNotBlank() } ?: "Login gagal. Coba lagi."
        }
    }
}
