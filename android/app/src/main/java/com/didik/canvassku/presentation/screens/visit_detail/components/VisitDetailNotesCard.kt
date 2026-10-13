package com.didik.canvassku.presentation.screens.visit_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.didik.canvassku.core.design.BrandOutline
import com.didik.canvassku.core.design.OnPrimaryFixedVariant
import com.didik.canvassku.core.design.OnSurface
import com.didik.canvassku.core.design.PrimaryFixed
import com.didik.canvassku.core.design.SurfaceContainerLow
import com.didik.canvassku.core.design.SurfaceContainerLowest

@Composable
fun VisitDetailNotesCard(
    orderNotes: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PrimaryFixed, RoundedCornerShape(percent = 50)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.ReceiptLong,
                    contentDescription = null,
                    tint = OnPrimaryFixedVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = "Catatan Pesanan",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
            )
        }
        Text(
            text = orderNotes,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurface,
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                .padding(12.dp),
        )
    }
}
