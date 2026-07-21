package com.shena.snehasacademy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.shena.snehasacademy.core.components.NoInternetOverlay
import com.shena.snehasacademy.core.navigation.SnehasNavHost
import com.shena.snehasacademy.core.theme.SnehasAcademyTheme
import com.shena.snehasacademy.core.utils.rememberIsOnline

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnehasAcademyTheme {
                Box(Modifier.fillMaxSize()) {
                    SnehasNavHost()
                    val isOnline by rememberIsOnline()
                    if (!isOnline) {
                        NoInternetOverlay()
                    }
                }
            }
        }
    }
}
