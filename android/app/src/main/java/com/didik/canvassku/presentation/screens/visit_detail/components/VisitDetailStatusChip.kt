package com.didik.canvassku.presentation.screens.visit_detail.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.didik.canvassku.core.design.BrandOutline
import com.didik.canvassku.core.design.ErrorContainer
import com.didik.canvassku.core.design.OnErrorContainer
import com.didik.canvassku.core.design.OnSecondaryContainer
import com.didik.canvassku.core.design.OnTertiaryFixedVariant
import com.didik.canvassku.core.design.SecondaryContainer
import com.didik.canvassku.core.design.TertiaryFixed
import com.didik.canvassku.domain.model.SyncStatus

@Composable
fun VisitDetailStatusChip(
    syncStatus: String,
    modifier: Modifier = Modifier,
) {
    val style = syncStatus.toChipStyle()
    AssistChip(
        onClick = {},
        enabled = false,
        label = {
            Text(
                text = style.label,
                style = MaterialTheme.typography.labelMedium,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = style.icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = style.content,
            )
        },
        shape = RoundedCornerShape(percent = 50),
        colors = AssistChipDefaults.assistChipColors(
            disabledContainerColor = style.container,
            disabledLabelColor = style.content,
            disabledLeadingIconContentColor = style.content,
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = false,
            borderColor = BrandOutline,
        ),
        modifier = modifier.heightIn(min = 32.dp),
    )
}

private data class StatusChipStyle(
    val label: String,
    val icon: ImageVector,
    val container: Color,
    val content: Color,
)

private fun String.toChipStyle(): StatusChipStyle {
    return when (this) {
        SyncStatus.SYNCED -> StatusChipStyle(
            label = "Tersinkronisasi",
            icon = Icons.Filled.CheckCircle,
            container = SecondaryContainer,
            content = OnSecondaryContainer,
        )
        SyncStatus.FAILED -> StatusChipStyle(
            label = "Gagal Sinkronisasi",
            icon = Icons.Filled.CloudOff,
            container = ErrorContainer,
            content = OnErrorContainer,
        )
        else -> StatusChipStyle(
            label = "Menunggu Sinkronisasi",
            icon = Icons.Filled.Sync,
            container = TertiaryFixed,
            content = OnTertiaryFixedVariant,
        )
    }
}
