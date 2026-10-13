package com.didik.canvassku.presentation.screens.visit_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.didik.canvassku.core.design.BrandOutline
import com.didik.canvassku.core.design.OnSurface
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.PrimaryContainer
import com.didik.canvassku.core.design.SurfaceContainer
import com.didik.canvassku.core.design.SurfaceContainerHigh
import com.didik.canvassku.core.design.SurfaceContainerLowest
import java.io.File

@Composable
fun VisitDetailPhotoCard(
    localImagePath: String,
    modifier: Modifier = Modifier,
) {
    val isRemote = localImagePath.startsWith("http://", ignoreCase = true) ||
        localImagePath.startsWith("https://", ignoreCase = true)
    val imageModel: Any? = when {
        localImagePath.isBlank() -> null
        isRemote -> localImagePath
        else -> File(localImagePath)
    }

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
                    .background(SurfaceContainerHigh, RoundedCornerShape(percent = 50)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = null,
                    tint = OnSurfaceVariant,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                text = "Foto Bukti Kunjungan",
                style = MaterialTheme.typography.titleMedium,
                color = OnSurface,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceContainer)
                .border(1.dp, BrandOutline.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (imageModel == null) {
                PhotoUnavailable(
                    message = "Foto tidak ditemukan di perangkat.",
                )
            } else {
                SubcomposeAsyncImage(
                    model = imageModel,
                    contentDescription = "Foto bukti kunjungan",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 2.dp,
                                color = PrimaryContainer,
                            )
                        }
                    },
                    error = {
                        PhotoUnavailable(
                            message = if (isRemote) {
                                "Foto tidak dapat dimuat dari server."
                            } else {
                                "Foto tidak ditemukan di perangkat."
                            },
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun PhotoUnavailable(message: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(24.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.BrokenImage,
            contentDescription = null,
            tint = OnSurfaceVariant,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
