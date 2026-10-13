package com.didik.canvassku.core.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/** Same as Manifest.permission.ACCESS_LOCAL_NETWORK (API 37+); string avoids minSdk lint. */
const val ACCESS_LOCAL_NETWORK_PERMISSION = "android.permission.ACCESS_LOCAL_NETWORK"

/** Android 17+ blocks LAN / emulator host (10.0.2.2) without this grant when targetSdk >= 37. */
fun Context.hasAccessLocalNetworkPermission(): Boolean {
    if (Build.VERSION.SDK_INT < 37) return true
    return ContextCompat.checkSelfPermission(
        this,
        ACCESS_LOCAL_NETWORK_PERMISSION,
    ) == PackageManager.PERMISSION_GRANTED
}

fun requiresAccessLocalNetworkRuntime(): Boolean = Build.VERSION.SDK_INT >= 37

@Composable
fun EnsureLocalNetworkPermission(
    onDenied: () -> Unit = {},
) {
    if (!requiresAccessLocalNetworkRuntime()) return

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (!granted) onDenied()
    }

    LaunchedEffect(Unit) {
        if (!context.hasAccessLocalNetworkPermission()) {
            launcher.launch(ACCESS_LOCAL_NETWORK_PERMISSION)
        }
    }
}
