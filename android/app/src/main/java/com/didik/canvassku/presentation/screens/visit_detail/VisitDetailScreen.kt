package com.didik.canvassku.presentation.screens.visit_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.didik.canvassku.core.design.Background
import com.didik.canvassku.core.design.BrandSecondary
import com.didik.canvassku.core.design.BrandSurfaceContainer
import com.didik.canvassku.core.design.OnSurface
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.OutlineVariant
import com.didik.canvassku.core.design.Primary
import com.didik.canvassku.core.design.PrimaryContainer
import com.didik.canvassku.core.design.SurfaceContainerLowest
import com.didik.canvassku.presentation.screens.visit_detail.components.VisitDetailHeaderCard
import com.didik.canvassku.presentation.screens.visit_detail.components.VisitDetailNotesCard
import com.didik.canvassku.presentation.screens.visit_detail.components.VisitDetailPhotoCard
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitDetailScreen(
    reportId: String,
    onNavigateBack: () -> Unit,
    viewModel: VisitDetailViewModel = koinViewModel { parametersOf(reportId) },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Kunjungan",
                        style = MaterialTheme.typography.titleLarge,
                        color = Primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = OnSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceContainerLowest,
                    titleContentColor = Primary,
                    navigationIconContentColor = OnSurface,
                ),
            )
        },
    ) { innerPadding ->
        when (val state = uiState) {
            VisitDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PrimaryContainer)
                }
            }

            VisitDetailState.NotFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Laporan kunjungan tidak ditemukan.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant,
                    )
                }
            }

            is VisitDetailState.Ready -> {
                val report = state.report
                val storeName = state.store?.name?.takeIf { it.isNotBlank() }
                    ?: "Toko tidak ditemukan"
                val lat = String.format(Locale.US, "%.5f", report.latitude)
                val lng = String.format(Locale.US, "%.5f", report.longitude)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(PaddingValues(horizontal = 16.dp, vertical = 16.dp)),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    VisitDetailHeaderCard(
                        storeName = storeName,
                        syncStatus = report.syncStatus,
                        createdAt = report.createdAt,
                    )

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
                        colors = AssistChipDefaults.assistChipColors(
                            disabledContainerColor = BrandSurfaceContainer,
                            disabledLabelColor = BrandSecondary,
                            disabledLeadingIconContentColor = BrandSecondary,
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            enabled = false,
                            borderColor = OutlineVariant,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 32.dp),
                    )

                    VisitDetailPhotoCard(localImagePath = report.localImagePath)

                    VisitDetailNotesCard(orderNotes = report.orderNotes)
                }
            }
        }
    }
}
