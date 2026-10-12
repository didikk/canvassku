package com.didik.canvassku.presentation.screens.auth

data class LoginState(
    val identifier: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val status: Status = Status.Idle,
) {
    sealed interface Status {
        data object Idle : Status
        data object Loading : Status
        data class Error(val message: String) : Status
    }
}

sealed interface LoginEffect {
    data object NavigateHome : LoginEffect
}
