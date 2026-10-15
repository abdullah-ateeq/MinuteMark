package com.example.minutemark.ui.home

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minutemark.data.model.MinuteMarkUiState
import com.example.minutemark.domain.MinuteMarkFormatter
import com.example.minutemark.ui.components.MinuteMarkTimePickerDialog
import com.example.minutemark.ui.components.TimePresetRow
import com.example.minutemark.ui.components.TimeToValueResultCard
import java.time.LocalTime

@Composable
fun TimeToValueTab(
    uiState: MinuteMarkUiState,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onNowClick: () -> Unit,
    onIncludeSecondsChange: (Boolean) -> Unit,
    onSecondsInputChange: (String) -> Unit,
    onPresetSelected: (LocalTime) -> Unit,
    onCalculate: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showTimePickerDialog by remember { mutableStateOf(false) }

    fun triggerHaptic() {
        if (uiState.settings.hapticFeedback) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    if (showTimePickerDialog) {
        MinuteMarkTimePickerDialog(
            initialTime = uiState.selectedTime,
            onConfirm = { h, m ->
                showTimePickerDialog = false
                triggerHaptic()
                onTimeSelected(h, m)
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Time selection card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    triggerHaptic()
                    showTimePickerDialog = true
                }
                .semantics {
                    contentDescription = "Selected time, ${MinuteMarkFormatter.formatTime12Hour(uiState.selectedTime, uiState.includeSeconds)}"
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CLOCK TIME",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            triggerHaptic()
                            onNowClick()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "NOW",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = MinuteMarkFormatter.formatTime12Hour(uiState.selectedTime, uiState.includeSeconds),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Tap to change time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick time presets
        Text(
            text = "QUICK TIMES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        TimePresetRow(
            onPresetSelected = { presetTime ->
                triggerHaptic()
                onPresetSelected(presetTime)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Include Seconds toggle
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Include Seconds",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Enable precision seconds conversion",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = uiState.includeSeconds,
                        onCheckedChange = {
                            triggerHaptic()
                            onIncludeSecondsChange(it)
                        }
                    )
                }

                if (uiState.includeSeconds) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = uiState.secondsInput,
                        onValueChange = onSecondsInputChange,
                        label = { Text("Seconds (0–59)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                triggerHaptic()
                                onCalculate()
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "Seconds, ${uiState.secondsInput}"
                            }
                    )
                }
            }
        }

        // Error message banner
        if (uiState.errorMessage != null && uiState.mode == com.example.minutemark.data.model.ConversionDirection.TIME_TO_VALUE) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action buttons: Calculate and Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    triggerHaptic()
                    onCalculate()
                },
                modifier = Modifier
                    .weight(2f)
                    .height(52.dp)
                    .semantics { contentDescription = "Convert time" },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "CALCULATE",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = {
                    triggerHaptic()
                    onReset()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("RESET")
            }
        }

        // Result Card
        if (uiState.result != null) {
            Spacer(modifier = Modifier.height(24.dp))
            TimeToValueResultCard(
                result = uiState.result,
                animated = uiState.settings.animations
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
