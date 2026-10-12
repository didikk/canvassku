package com.didik.canvassku.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.didik.canvassku.core.design.OnPrimary
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.OnTertiaryContainer
import com.didik.canvassku.core.design.PrimaryContainer
import com.didik.canvassku.core.design.SurfaceContainerLow
import com.didik.canvassku.presentation.screens.home.StoreFilter

@Composable
fun StoreFilterChips(
    selected: StoreFilter,
    allCount: Int,
    notVisitedCount: Int,
    pendingSyncCount: Int,
    syncedCount: Int,
    onSelected: (StoreFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FilterChip(
            label = "Semua ($allCount)",
            selected = selected == StoreFilter.ALL,
            leadingCheck = selected == StoreFilter.ALL,
            onClick = { onSelected(StoreFilter.ALL) },
        )
        FilterChip(
            label = "Belum Dikunjungi ($notVisitedCount)",
            selected = selected == StoreFilter.NOT_VISITED,
            onClick = { onSelected(StoreFilter.NOT_VISITED) },
        )
        FilterChip(
            label = "Menunggu Kirim ($pendingSyncCount)",
            selected = selected == StoreFilter.PENDING_SYNC,
            leadingDot = true,
            onClick = { onSelected(StoreFilter.PENDING_SYNC) },
        )
        FilterChip(
            label = "Tersinkronisasi ($syncedCount)",
            selected = selected == StoreFilter.SYNCED,
            onClick = { onSelected(StoreFilter.SYNCED) },
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingCheck: Boolean = false,
    leadingDot: Boolean = false,
) {
    Row(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(if (selected) PrimaryContainer else SurfaceContainerLow)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        when {
            leadingCheck -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = OnPrimary,
                modifier = Modifier.size(16.dp),
            )
            leadingDot && !selected -> Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(OnTertiaryContainer, CircleShape),
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) OnPrimary else OnSurfaceVariant,
        )
    }
}
