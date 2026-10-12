package com.didik.canvassku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.didik.canvassku.core.design.CanvasskuTheme
import com.didik.canvassku.presentation.navigation.CanvasskuNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanvasskuTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    CanvasskuNavHost()
                }
            }
        }
    }
}
