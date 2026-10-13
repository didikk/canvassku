package com.didik.canvassku.presentation.screens.home.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.didik.canvassku.core.design.Error
import com.didik.canvassku.core.design.OnSurface
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.Primary

@Composable
fun LogoutConfirmDialog(
    pendingReportCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Keluar?",
                style = MaterialTheme.typography.titleLarge,
                color = OnSurface,
            )
        },
        text = {
            Text(
                text = if (pendingReportCount > 0) {
                    "Ada $pendingReportCount laporan belum terkirim. Keluar akan menghapus data lokal tersebut."
                } else {
                    "Keluar akan menghapus data lokal, termasuk laporan yang belum tersinkronisasi."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Keluar",
                    color = Error,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    color = Primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
    )
}
