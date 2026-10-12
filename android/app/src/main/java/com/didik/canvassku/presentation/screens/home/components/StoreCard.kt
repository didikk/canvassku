package com.didik.canvassku.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.didik.canvassku.core.design.OnSecondaryContainer
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.OnTertiaryContainer
import com.didik.canvassku.core.design.OnTertiaryFixedVariant
import com.didik.canvassku.core.design.Primary
import com.didik.canvassku.core.design.PrimaryContainer
import com.didik.canvassku.core.design.Secondary
import com.didik.canvassku.core.design.SecondaryContainer
import com.didik.canvassku.core.design.SurfaceContainer
import com.didik.canvassku.core.design.SurfaceContainerHigh
import com.didik.canvassku.core.design.SurfaceContainerLowest
import com.didik.canvassku.core.design.TertiaryFixed
import com.didik.canvassku.domain.model.StoreListItem
import com.didik.canvassku.domain.model.StoreVisitStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun StoreCard(
    item: StoreListItem,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusStyle = item.status.toStyle()

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
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .background(statusStyle.badgeBackground, RoundedCornerShape(percent = 50))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                imageVector = statusStyle.badgeIcon,
                contentDescription = null,
                tint = statusStyle.badgeContent,
                modifier = Modifier.size(14.dp),
            )
            Text(
                text = statusStyle.label,
                style = MaterialTheme.typography.labelMedium,
                color = statusStyle.badgeContent,
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(statusStyle.iconBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Storefront,
                    contentDescription = null,
                    tint = statusStyle.iconTint,
                    modifier = Modifier.size(26.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.store.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (!item.store.address.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.padding(top = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = OnSurfaceVariant,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = item.store.address,
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                item.statusDetailText()?.let { detail ->
                    Text(
                        text = detail,
                        style = MaterialTheme.typography.labelMedium,
                        color = statusStyle.detailColor,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
        }

        Button(
            onClick = onActionClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(percent = 50),
            colors = ButtonDefaults.buttonColors(
                containerColor = SurfaceContainerHigh,
                contentColor = Primary,
            ),
        ) {
            Text(
                text = statusStyle.actionLabel,
                style = MaterialTheme.typography.labelLarge,
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

private data class StoreStatusStyle(
    val label: String,
    val badgeIcon: ImageVector,
    val badgeBackground: Color,
    val badgeContent: Color,
    val iconBackground: Color,
    val iconTint: Color,
    val detailColor: Color,
    val actionLabel: String,
)

private fun StoreListItem.statusDetailText(): String? {
    val createdAt = latestReportCreatedAt ?: return null
    val time = WIB_TIME_FORMAT.format(Date(createdAt))
    return when (status) {
        StoreVisitStatus.PENDING_SYNC -> "Laporan tersimpan: $time WIB (Draft)"
        StoreVisitStatus.SYNCED -> "Selesai dikunjungi: $time WIB"
        StoreVisitStatus.NOT_VISITED -> null
    }
}

private val WIB_TIME_FORMAT = SimpleDateFormat("HH:mm", Locale.forLanguageTag("id-ID")).apply {
    timeZone = TimeZone.getTimeZone("Asia/Jakarta")
}

private fun StoreVisitStatus.toStyle(): StoreStatusStyle {
    return when (this) {
        StoreVisitStatus.NOT_VISITED -> StoreStatusStyle(
            label = "Belum Dikunjungi",
            badgeIcon = Icons.Filled.Schedule,
            badgeBackground = SurfaceContainer,
            badgeContent = OnSurfaceVariant,
            iconBackground = SurfaceContainer,
            iconTint = Primary,
            detailColor = OnSurfaceVariant,
            actionLabel = "Mulai Kunjungan",
        )
        StoreVisitStatus.PENDING_SYNC -> StoreStatusStyle(
            label = "Menunggu Kirim",
            badgeIcon = Icons.Filled.CloudUpload,
            badgeBackground = TertiaryFixed,
            badgeContent = OnTertiaryFixedVariant,
            iconBackground = TertiaryFixed.copy(alpha = 0.4f),
            iconTint = OnTertiaryFixedVariant,
            detailColor = OnTertiaryContainer,
            actionLabel = "Lihat Detail",
        )
        StoreVisitStatus.SYNCED -> StoreStatusStyle(
            label = "Tersinkronisasi",
            badgeIcon = Icons.Filled.CheckCircle,
            badgeBackground = SecondaryContainer,
            badgeContent = OnSecondaryContainer,
            iconBackground = SecondaryContainer.copy(alpha = 0.4f),
            iconTint = Secondary,
            detailColor = Secondary,
            actionLabel = "Lihat Detail",
        )
    }
}
