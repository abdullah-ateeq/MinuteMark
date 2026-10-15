package com.example.minutemark

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.minutemark.ui.navigation.AppNavigation
import com.example.minutemark.ui.theme.MinuteMarkTheme
import com.example.minutemark.ui.viewmodel.MinuteMarkViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MinuteMarkViewModel = viewModel(
                factory = MinuteMarkViewModel.provideFactory(this)
            )
            val uiState by viewModel.uiState.collectAsState()

            MinuteMarkTheme(
                appTheme = uiState.settings.theme,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}