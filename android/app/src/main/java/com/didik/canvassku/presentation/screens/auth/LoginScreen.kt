package com.didik.canvassku.presentation.screens.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.didik.canvassku.core.design.OnPrimary
import com.didik.canvassku.core.design.OnSurface
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.Outline
import com.didik.canvassku.core.design.PrimaryContainer
import com.didik.canvassku.core.design.Surface
import com.didik.canvassku.core.design.SurfaceContainerLow
import com.didik.canvassku.core.design.SurfaceContainerLowest
import com.didik.canvassku.core.util.ACCESS_LOCAL_NETWORK_PERMISSION
import com.didik.canvassku.core.util.hasAccessLocalNetworkPermission
import com.didik.canvassku.core.util.requiresAccessLocalNetworkRuntime
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onNavigateHome: () -> Unit,
    sessionExpiredMessage: String? = null,
    onClearSessionMessage: () -> Unit = {},
    viewModel: LoginViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val isLoading = uiState.status is LoginState.Status.Loading
    var localNetworkDenied by remember { mutableStateOf(false) }
    val errorMessage = (uiState.status as? LoginState.Status.Error)?.message
        ?: sessionExpiredMessage
        ?: if (localNetworkDenied) {
            "Izin jaringan lokal diperlukan untuk terhubung ke server pengembangan."
        } else {
            null
        }

    val localNetworkLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            localNetworkDenied = false
            viewModel.onLoginClick()
        } else {
            localNetworkDenied = true
        }
    }

    val submitLogin: () -> Unit = {
        focusManager.clearFocus()
        localNetworkDenied = false
        onClearSessionMessage()
        if (!requiresAccessLocalNetworkRuntime() || context.hasAccessLocalNetworkPermission()) {
            viewModel.onLoginClick()
        } else {
            localNetworkLauncher.launch(ACCESS_LOCAL_NETWORK_PERMISSION)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateHome -> onNavigateHome()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Surface)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color(0x1A1A202C),
                        spotColor = Color(0x141A202C),
                    )
                    .background(SurfaceContainerLowest, RoundedCornerShape(24.dp))
                    .padding(24.dp),
            ) {
                Text(
                    text = "Canvassku",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryContainer,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Masuk ke Akun Sales Anda",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Nama Pengguna / Email",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
                )
                OutlinedTextField(
                    value = uiState.identifier,
                    onValueChange = { value ->
                        if (sessionExpiredMessage != null) onClearSessionMessage()
                        viewModel.onIdentifierChange(value)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("contoh: sales@canvassku.id") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Badge,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                        )
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = loginFieldColors(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Kata Sandi",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
                )
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { value ->
                        if (sessionExpiredMessage != null) onClearSessionMessage()
                        viewModel.onPasswordChange(value)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = { Text("••••••••") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible) {
                                    Icons.Filled.VisibilityOff
                                } else {
                                    Icons.Filled.Visibility
                                },
                                contentDescription = if (uiState.isPasswordVisible) {
                                    "Sembunyikan kata sandi"
                                } else {
                                    "Tampilkan kata sandi"
                                },
                                tint = OnSurfaceVariant,
                            )
                        }
                    },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = if (uiState.isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { submitLogin() },
                    ),
                    shape = RoundedCornerShape(12.dp),
                    colors = loginFieldColors(),
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = submitLogin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(percent = 50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainer,
                        contentColor = OnPrimary,
                    ),
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = OnPrimary,
                        )
                    } else {
                        Text(
                            text = "Masuk",
                            style = MaterialTheme.typography.labelLarge,
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            LoginNetworkTip()
        }
    }
}

@Composable
private fun loginFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = SurfaceContainerLow,
    unfocusedContainerColor = SurfaceContainerLow,
    disabledContainerColor = SurfaceContainerLow,
    focusedBorderColor = Color.Transparent,
    unfocusedBorderColor = Color.Transparent,
    disabledBorderColor = Color.Transparent,
    focusedTextColor = OnSurface,
    unfocusedTextColor = OnSurface,
    cursorColor = PrimaryContainer,
    focusedPlaceholderColor = Outline,
    unfocusedPlaceholderColor = Outline,
    focusedLeadingIconColor = OnSurfaceVariant,
    unfocusedLeadingIconColor = OnSurfaceVariant,
)
