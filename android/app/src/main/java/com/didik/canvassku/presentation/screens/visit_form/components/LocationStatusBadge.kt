package com.didik.canvassku.presentation.screens.visit_form.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.didik.canvassku.core.design.BrandSecondary
import com.didik.canvassku.core.design.BrandSurfaceContainer
import com.didik.canvassku.core.design.BrandTertiary
import com.didik.canvassku.core.design.Error
import com.didik.canvassku.core.design.OutlineVariant
import com.didik.canvassku.core.design.PrimaryContainer
import com.didik.canvassku.core.design.SurfaceContainerLowest
import com.didik.canvassku.presentation.screens.visit_form.VisitFormState
import java.util.Locale

@Composable
fun LocationStatusBadge(
    location: VisitFormState.LocationUi,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (location) {
            VisitFormState.LocationUi.Loading -> {
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            text = "Mengunci GPS...",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    leadingIcon = {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryContainer,
                        )
                    },
                    shape = RoundedCornerShape(percent = 50),
                    colors = chipColors(BrandTertiary),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = false,
                        borderColor = OutlineVariant,
                    ),
                )
            }

            VisitFormState.LocationUi.PermissionRequired -> {
                AssistChip(
                    onClick = onRetryClick,
                    label = {
                        Text(
                            text = "Izinkan lokasi",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOff,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    shape = RoundedCornerShape(percent = 50),
                    colors = chipColors(Error),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = OutlineVariant,
                    ),
                    modifier = Modifier.heightIn(min = 32.dp),
                )
            }

            is VisitFormState.LocationUi.Ready -> {
                val lat = String.format(Locale.US, "%.5f", location.latitude)
                val lng = String.format(Locale.US, "%.5f", location.longitude)
                AssistChip(
                    onClick = {},
                    enabled = false,
                    label = {
                        Text(
                            text = "GPS $lat, $lng",
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = BrandSecondary,
                        )
                    },
                    shape = RoundedCornerShape(percent = 50),
                    colors = chipColors(BrandSecondary),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = false,
                        borderColor = OutlineVariant,
                    ),
                )
            }

            is VisitFormState.LocationUi.Error -> {
                AssistChip(
                    onClick = onRetryClick,
                    label = {
                        Text(
                            text = location.message,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                    shape = RoundedCornerShape(percent = 50),
                    colors = chipColors(Error),
                    border = AssistChipDefaults.assistChipBorder(
                        enabled = true,
                        borderColor = OutlineVariant,
                    ),
                    modifier = Modifier.heightIn(min = 48.dp),
                )
                TextButton(onClick = onRetryClick) {
                    Text(
                        text = "Coba lagi",
                        style = MaterialTheme.typography.labelLarge,
                        color = PrimaryContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun chipColors(labelColor: androidx.compose.ui.graphics.Color) =
    AssistChipDefaults.assistChipColors(
        containerColor = SurfaceContainerLowest,
        labelColor = labelColor,
        leadingIconContentColor = labelColor,
        disabledContainerColor = BrandSurfaceContainer,
        disabledLabelColor = labelColor,
        disabledLeadingIconContentColor = labelColor,
    )
