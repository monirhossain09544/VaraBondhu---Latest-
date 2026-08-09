package com.rork.varabondhu

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rork.varabondhu.ui.navigation.AppNavigation
import com.mapbox.common.MapboxOptions
import com.rork.varabondhu.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Map setup must never prevent the app from launching, so a failure here is
        // logged and the app continues with map features degraded.
        runCatching { MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN }
            .onFailure { error -> Log.e(TAG, "Mapbox initialization failed", error) }
        // The splash canvas is near-white at the top, so system bar icons stay dark.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.BLACK)
        )
        setContent {
            AppTheme {
                AppNavigation()
            }
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}
