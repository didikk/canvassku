package com.didik.canvassku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.didik.canvassku.core.design.CanvasskuTheme
import com.didik.canvassku.domain.repository.AuthRepository
import com.didik.canvassku.presentation.navigation.CanvasskuNavHost
import com.didik.canvassku.presentation.navigation.NavRoute
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val authRepository: AuthRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var keepSplash by mutableStateOf(true)
        var startRoute by mutableStateOf<NavRoute?>(null)

        splashScreen.setKeepOnScreenCondition { keepSplash }

        lifecycleScope.launch {
            val isLoggedIn = authRepository.hasValidSession()
            startRoute = if (isLoggedIn) NavRoute.Home else NavRoute.Login
            keepSplash = false
        }

        enableEdgeToEdge()
        setContent {
            CanvasskuTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    startRoute?.let { CanvasskuNavHost(startRoute = it) }
                }
            }
        }
    }
}
