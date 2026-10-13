package com.didik.canvassku.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.didik.canvassku.core.design.Background
import com.didik.canvassku.core.design.OnSurfaceVariant
import com.didik.canvassku.core.design.Primary
import com.didik.canvassku.core.design.PrimaryContainer
import com.didik.canvassku.core.design.SurfaceContainerLowest
import com.didik.canvassku.presentation.screens.home.components.EmptyStoresState
import com.didik.canvassku.presentation.screens.home.components.LogoutConfirmDialog
import com.didik.canvassku.presentation.screens.home.components.PendingSyncBanner
import com.didik.canvassku.presentation.screens.home.components.StoreCard
import com.didik.canvassku.presentation.screens.home.components.StoreFilterChips
import com.didik.canvassku.presentation.screens.home.components.SyncActionIcon
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onStartVisit: (storeId: String) -> Unit,
    onViewDetail: (reportId: String) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HomeEffect.LoggedOut -> onLogout()
                is HomeEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
                is HomeEffect.OpenVisitForm -> onStartVisit(effect.storeId)
                is HomeEffect.OpenVisitDetail -> onViewDetail(effect.reportId)
            }
        }
    }

    if (uiState.showLogoutConfirm) {
        LogoutConfirmDialog(
            pendingReportCount = uiState.pendingReportCount,
            onConfirm = viewModel::onLogoutConfirm,
            onDismiss = viewModel::onLogoutDismiss,
        )
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Daftar Toko",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary,
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = viewModel::onSyncClick,
                            enabled = !uiState.isSyncing,
                        ) {
                            SyncActionIcon(
                                hasPending = uiState.pendingReportCount > 0,
                            )
                        }
                        IconButton(onClick = viewModel::onLogoutClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Keluar",
                                tint = OnSurfaceVariant,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SurfaceContainerLowest,
                        titleContentColor = Primary,
                        actionIconContentColor = OnSurfaceVariant,
                    ),
                )
                if (uiState.isSyncing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = PrimaryContainer,
                        trackColor = PrimaryContainer.copy(alpha = 0.15f),
                    )
                }
            }
        },
    ) { innerPadding ->
        when {
            uiState.stores.isEmpty() && uiState.isSyncing -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = PrimaryContainer)
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    if (uiState.pendingReportCount > 0) {
                        item(key = "pending-banner") {
                            PendingSyncBanner(
                                pendingCount = uiState.pendingReportCount,
                                onSendNowClick = viewModel::onSendPendingClick,
                            )
                        }
                    }

                    item(key = "filters") {
                        StoreFilterChips(
                            selected = uiState.filter,
                            allCount = uiState.allCount,
                            notVisitedCount = uiState.notVisitedCount,
                            pendingSyncCount = uiState.pendingSyncCount,
                            syncedCount = uiState.syncedCount,
                            onSelected = viewModel::onFilterSelected,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    if (uiState.filteredStores.isEmpty()) {
                        item(key = "empty") {
                            EmptyStoresState(
                                hasStores = uiState.stores.isNotEmpty(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp, horizontal = 24.dp),
                            )
                        }
                    } else {
                        items(uiState.filteredStores, key = { it.store.id }) { item ->
                            StoreCard(
                                item = item,
                                onActionClick = { viewModel.onStoreActionClick(item) },
                            )
                        }
                    }
                }
            }
        }
    }
}
