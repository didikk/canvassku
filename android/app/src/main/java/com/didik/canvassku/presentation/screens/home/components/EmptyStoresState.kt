package com.didik.canvassku.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.didik.canvassku.core.design.OnSurface
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.SurfaceContainer

@Composable
fun EmptyStoresState(
    hasStores: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(SurfaceContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (hasStores) "Tidak Ada Toko" else "Belum ada data toko",
            style = MaterialTheme.typography.titleMedium,
            color = OnSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (hasStores) {
                "Tidak ada toko dengan kriteria filter yang dipilih pada rute ini."
            } else {
                "Ketuk ikon sync di atas untuk memuat daftar toko dari server."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
