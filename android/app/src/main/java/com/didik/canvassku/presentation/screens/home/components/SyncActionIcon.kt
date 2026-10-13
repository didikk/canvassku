package com.didik.canvassku.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.didik.canvassku.core.design.Error
import com.didik.canvassku.core.design.OnTertiaryContainer

@Composable
fun SyncActionIcon(
    hasPending: Boolean,
) {
    Box(contentAlignment = Alignment.TopEnd) {
        Icon(
            imageVector = Icons.Filled.Sync,
            contentDescription = "Sinkronisasi toko",
            tint = OnTertiaryContainer,
        )
        if (hasPending) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Error, CircleShape),
            )
        }
    }
}
