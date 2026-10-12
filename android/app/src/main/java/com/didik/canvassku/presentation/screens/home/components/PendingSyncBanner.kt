package com.didik.canvassku.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.didik.canvassku.core.design.OnTertiaryFixed
import com.didik.canvassku.core.design.OnTertiaryFixedVariant
import com.didik.canvassku.core.design.TertiaryContainer
import com.didik.canvassku.core.design.TertiaryFixed
import com.didik.canvassku.core.design.TertiaryFixedDim

@Composable
fun PendingSyncBanner(
    pendingCount: Int,
    onSendNowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TertiaryFixed, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(TertiaryFixedDim.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.SyncProblem,
                    contentDescription = null,
                    tint = OnTertiaryFixedVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$pendingCount Laporan belum terkirim",
                    style = MaterialTheme.typography.labelLarge,
                    color = OnTertiaryFixed,
                )
                Text(
                    text = "Tersimpan di memori perangkat",
                    style = MaterialTheme.typography.labelMedium,
                    color = OnTertiaryFixedVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(
                onClick = onSendNowClick,
                shape = RoundedCornerShape(percent = 50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TertiaryContainer,
                    contentColor = TertiaryFixed,
                ),
            ) {
                Text(
                    text = "Kirim Sekarang",
                    style = MaterialTheme.typography.labelLarge,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(16.dp),
                )
            }
        }
    }
}
