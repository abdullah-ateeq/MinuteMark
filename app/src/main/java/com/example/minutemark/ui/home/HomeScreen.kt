package com.example.minutemark.ui.home

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minutemark.data.model.ConversionDirection
import com.example.minutemark.data.model.MinuteMarkUiState
import com.example.minutemark.ui.viewmodel.MinuteMarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MinuteMarkViewModel,
    uiState: MinuteMarkUiState,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    fun triggerHaptic() {
        if (uiState.settings.hapticFeedback) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "MinuteMark",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Convert clock times into total minutes or seconds.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Mode Tabs: TIME -> VALUE vs VALUE -> TIME
            PrimaryTabRow(
                selectedTabIndex = if (uiState.mode == ConversionDirection.TIME_TO_VALUE) 0 else 1,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = uiState.mode == ConversionDirection.TIME_TO_VALUE,
                    onClick = {
                        triggerHaptic()
                        viewModel.setMode(ConversionDirection.TIME_TO_VALUE)
                    },
                    text = {
                        Text(
                            text = "TIME → VALUE",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                )
                Tab(
                    selected = uiState.mode == ConversionDirection.VALUE_TO_TIME,
                    onClick = {
                        triggerHaptic()
                        viewModel.setMode(ConversionDirection.VALUE_TO_TIME)
                    },
                    text = {
                        Text(
                            text = "VALUE → TIME",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (uiState.mode) {
                ConversionDirection.TIME_TO_VALUE -> {
                    TimeToValueTab(
                        uiState = uiState,
                        onTimeSelected = { h, m -> viewModel.setTime(h, m) },
                        onNowClick = { viewModel.setNow() },
                        onIncludeSecondsChange = { viewModel.setIncludeSeconds(it) },
                        onSecondsInputChange = { viewModel.setSecondsInput(it) },
                        onPresetSelected = { viewModel.applyTimePreset(it) },
                        onCalculate = { viewModel.calculate() },
                        onReset = { viewModel.reset() }
                    )
                }
                ConversionDirection.VALUE_TO_TIME -> {
                    ValueToTimeTab(
                        uiState = uiState,
                        onValueChange = { viewModel.setValueInput(it) },
                        onUnitChange = { viewModel.setValueUnit(it) },
                        onPresetSelected = { viewModel.applyValuePreset(it) },
                        onConvert = { viewModel.calculateReverse() },
                        onReset = { viewModel.reset() }
                    )
                }
            }
        }
    }
}
