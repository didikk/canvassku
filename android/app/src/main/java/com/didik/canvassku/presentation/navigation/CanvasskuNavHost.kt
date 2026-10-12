package com.didik.canvassku.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay

@Composable
fun CanvasskuNavHost(startRoute: NavRoute = NavRoute.Login) {
    val backStack = rememberNavBackStack(startRoute)

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
                RoutePlaceholder(label = "Login")
            }
            entry<NavRoute.Home> {
                RoutePlaceholder(label = "Home")
            }
            entry<NavRoute.VisitForm> { key ->
                RoutePlaceholder(label = "VisitForm:${key.storeId}")
            }
            entry<NavRoute.VisitDetail> { key ->
                RoutePlaceholder(label = "VisitDetail:${key.reportId}")
            }
        },
    )
}

@Composable
private fun RoutePlaceholder(label: String) {
    // Honest scaffold until Phase 3+ screens land; labels mark the unresolved destination.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "[SCREEN] $label")
    }
}
