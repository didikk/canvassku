package com.didik.canvassku.presentation.screens.visit_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
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
import com.didik.canvassku.core.design.OnSurface
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.SurfaceContainerLowest
import com.didik.canvassku.core.design.SurfaceTint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun VisitDetailHeaderCard(
    storeName: String,
    syncStatus: String,
    createdAt: Long,
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
        VisitDetailStatusChip(syncStatus = syncStatus)
        Text(
            text = storeName,
            style = MaterialTheme.typography.titleLarge,
            color = OnSurface,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Schedule,
                contentDescription = null,
                tint = SurfaceTint,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = WIB_DATE_TIME_FORMAT.format(Date(createdAt)),
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )
        }
    }
}

private val WIB_DATE_TIME_FORMAT = SimpleDateFormat(
    "d MMM yyyy, HH:mm 'WIB'",
    Locale.forLanguageTag("id-ID"),
).apply {
    timeZone = TimeZone.getTimeZone("Asia/Jakarta")
}
