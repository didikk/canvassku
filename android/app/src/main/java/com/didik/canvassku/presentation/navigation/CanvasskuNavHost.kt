package com.didik.canvassku.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.didik.canvassku.core.util.EnsureLocalNetworkPermission
import com.didik.canvassku.data.remote.AuthEffect
import com.didik.canvassku.data.remote.AuthEventBus
import com.didik.canvassku.presentation.screens.auth.LoginScreen
import com.didik.canvassku.presentation.screens.home.HomeScreen
import com.didik.canvassku.presentation.screens.visit_form.VisitFormScreen
import org.koin.compose.koinInject

@Composable
fun CanvasskuNavHost(startRoute: NavRoute) {
    val backStack = rememberNavBackStack(startRoute)
    val authEventBus = koinInject<AuthEventBus>()
    val snackbarHostState = remember { SnackbarHostState() }
    var sessionExpiredMessage by remember { mutableStateOf<String?>(null) }

    // targetSdk 37+: LAN / 10.0.2.2 needs ACCESS_LOCAL_NETWORK or OkHttp times out.
    EnsureLocalNetworkPermission()

    LaunchedEffect(authEventBus) {
        authEventBus.effects.collect { effect ->
            when (effect) {
                AuthEffect.ForceReLogin -> {
                    sessionExpiredMessage = "Sesi Anda telah berakhir. Silakan login kembali."
                    backStack.clear()
                    backStack.add(NavRoute.Login)
                    snackbarHostState.showSnackbar(sessionExpiredMessage!!)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
            },
            entryProvider = entryProvider {
                entry<NavRoute.Login> {
                    LoginScreen(
                        sessionExpiredMessage = sessionExpiredMessage,
                        onClearSessionMessage = { sessionExpiredMessage = null },
                        onNavigateHome = {
                            sessionExpiredMessage = null
                            backStack.clear()
                            backStack.add(NavRoute.Home)
                        },
                    )
                }
                entry<NavRoute.Home> {
                    HomeScreen(
                        onLogout = {
                            backStack.clear()
                            backStack.add(NavRoute.Login)
                        },
                        onStartVisit = { storeId ->
                            backStack.add(NavRoute.VisitForm(storeId))
                        },
                        onViewDetail = { reportId ->
                            backStack.add(NavRoute.VisitDetail(reportId))
                        },
                    )
                }
                entry<NavRoute.VisitForm> { key ->
                    VisitFormScreen(
                        storeId = key.storeId,
                        onNavigateBack = { backStack.removeLastOrNull() },
                    )
                }
                entry<NavRoute.VisitDetail> { key ->
                    RoutePlaceholder(label = "VisitDetail:${key.reportId}")
                }
            },
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun RoutePlaceholder(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "[SCREEN] $label")
    }
}
