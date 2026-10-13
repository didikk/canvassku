package com.didik.canvassku.presentation.screens.visit_form

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.didik.canvassku.core.design.Background
import com.didik.canvassku.core.design.BrandOutline
import com.didik.canvassku.core.design.OnPrimary
import com.didik.canvassku.core.design.OnSurface
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.Outline
import com.didik.canvassku.core.design.Primary
import com.didik.canvassku.core.design.PrimaryContainer
import com.didik.canvassku.core.design.SurfaceContainerLow
import com.didik.canvassku.core.design.SurfaceContainerLowest
import com.didik.canvassku.presentation.screens.visit_form.components.LocationStatusBadge
import com.didik.canvassku.presentation.screens.visit_form.components.VisitPhotoSection
import com.didik.canvassku.presentation.screens.visit_form.components.VisitStoreHeader
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitFormScreen(
    storeId: String,
    onNavigateBack: () -> Unit,
    viewModel: VisitFormViewModel = koinViewModel { parametersOf(storeId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    val isSaving = uiState.status is VisitFormState.Status.Saving
    val errorMessage = (uiState.status as? VisitFormState.Status.Error)?.message

    var pendingCameraFile by remember { mutableStateOf<File?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        viewModel.onLocationPermissionResult(granted)
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = TakePictureWithGrant(),
    ) { success ->
        val file = pendingCameraFile
        pendingCameraFile = null
        if (success && file != null && file.length() > 0L) {
            viewModel.onPhotoCaptured(file.absolutePath)
        } else {
            file?.delete()
            if (success) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        "Kamera tidak menghasilkan foto. Cek kamera perangkat atau AVD.",
                    )
                }
            }
        }
    }

    val launchCamera: () -> Unit = {
        if (!context.canLaunchImageCapture()) {
            scope.launch {
                snackbarHostState.showSnackbar("Kamera tidak tersedia di perangkat ini.")
            }
        } else {
            val cameraDir = File(context.cacheDir, "camera").apply { mkdirs() }
            val photoFile = File(cameraDir, "capture_${System.currentTimeMillis()}.jpg")
            photoFile.createNewFile()
            pendingCameraFile = photoFile
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile,
            )
            takePictureLauncher.launch(uri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Izin kamera diperlukan untuk foto kunjungan.")
            }
        }
    }

    val requestCameraThenLaunch: () -> Unit = {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                VisitFormEffect.NavigateBack -> onNavigateBack()
                VisitFormEffect.RequestLocationPermission -> {
                    val fineGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED
                    val coarseGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    ) == PackageManager.PERMISSION_GRANTED
                    if (fineGranted || coarseGranted) {
                        viewModel.onLocationPermissionResult(true)
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                            ),
                        )
                    }
                }
                is VisitFormEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Form Kunjungan",
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !isSaving) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = OnSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest,
                    titleContentColor = Primary,
                    navigationIconContentColor = OnSurface,
                ),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(PaddingValues(horizontal = 16.dp, vertical = 16.dp)),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                VisitStoreHeader(store = uiState.store)

                LocationStatusBadge(
                    location = uiState.location,
                    onRetryClick = viewModel::onRetryLocationClick,
                )

                VisitPhotoSection(
                    localImagePath = uiState.localImagePath,
                    enabled = !isSaving,
                    onCaptureClick = { requestCameraThenLaunch() },
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(16.dp),
                            ambientColor = Color(0x141A202C),
                            spotColor = Color(0x0A1A202C),
                        )
                        .background(SurfaceContainerLowest, RoundedCornerShape(16.dp))
                        .border(1.dp, BrandOutline, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Inventory2,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(22.dp),
                        )
                        Text(
                            text = "Catatan Pesanan",
                            style = MaterialTheme.typography.titleMedium,
                            color = OnSurface,
                        )
                    }
                    Text(
                        text = "Daftar Produk & Kuantitas",
                        style = MaterialTheme.typography.labelMedium,
                        color = PrimaryContainer,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                    OutlinedTextField(
                        value = uiState.orderNotes,
                        onValueChange = viewModel::onOrderNotesChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 140.dp),
                        placeholder = {
                            Text("Tuliskan detail produk dan jumlah pesanan...")
                        },
                        enabled = !isSaving,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceContainerLow,
                            unfocusedContainerColor = SurfaceContainerLow,
                            disabledContainerColor = SurfaceContainerLow,
                            focusedBorderColor = PrimaryContainer,
                            unfocusedBorderColor = BrandOutline,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = PrimaryContainer,
                            focusedPlaceholderColor = Outline,
                            unfocusedPlaceholderColor = Outline,
                        ),
                    )
                    Text(
                        text = "Tuliskan detail produk dan jumlah pesanan.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnSurfaceVariant,
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.onSubmitClick()
                        },
                        enabled = uiState.canSubmit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(percent = 50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimary,
                        ),
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = OnPrimary,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                            Text(
                                text = "Kirim Laporan",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.OfflineBolt,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = "Data tersimpan lokal jika sinyal terputus",
                            style = MaterialTheme.typography.labelMedium,
                            color = OnSurfaceVariant,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
