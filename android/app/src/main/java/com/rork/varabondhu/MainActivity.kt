package com.rork.varabondhu

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rork.varabondhu.ui.localization.AppLanguageProvider
import com.rork.varabondhu.ui.localization.LanguageViewModel
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
            val languageViewModel: LanguageViewModel = viewModel()
            val language by languageViewModel.language.collectAsStateWithLifecycle()
            AppLanguageProvider(language = language) {
                AppTheme {
                    AppNavigation(
                        language = language,
                        onLanguageSelected = languageViewModel::selectLanguage
                    )
                }
            }
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}
