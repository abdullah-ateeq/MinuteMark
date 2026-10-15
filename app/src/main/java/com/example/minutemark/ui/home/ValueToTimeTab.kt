package com.example.minutemark.ui.home

import android.view.HapticFeedbackConstants
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.minutemark.data.model.ConversionDirection
import com.example.minutemark.data.model.MinuteMarkUiState
import com.example.minutemark.data.model.ValueUnit
import com.example.minutemark.ui.components.ValuePresetRow
import com.example.minutemark.ui.components.ValueToTimeResultCard

@Composable
fun ValueToTimeTab(
    uiState: MinuteMarkUiState,
    onValueChange: (String) -> Unit,
    onUnitChange: (ValueUnit) -> Unit,
    onPresetSelected: (Long) -> Unit,
    onConvert: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    fun triggerHaptic() {
        if (uiState.settings.hapticFeedback) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Numeric value input card
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                Text(
                    text = "ENTER NUMERICAL VALUE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.valueInput,
                    onValueChange = onValueChange,
                    label = {
                        Text(
                            if (uiState.valueUnit == ValueUnit.MINUTES) "Total Minutes" else "Total Seconds"
                        )
                    },
                    placeholder = {
                        Text(if (uiState.valueUnit == ValueUnit.MINUTES) "570" else "34200")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            triggerHaptic()
                            onConvert()
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "Conversion value, ${uiState.valueInput}"
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Unit Selector
                Text(
                    text = "UNIT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "Unit, ${uiState.valueUnit.name.lowercase()}"
                        },
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.valueUnit == ValueUnit.MINUTES,
                        onClick = {
                            triggerHaptic()
                            onUnitChange(ValueUnit.MINUTES)
                        },
                        label = { Text("MINUTES", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                    FilterChip(
                        selected = uiState.valueUnit == ValueUnit.SECONDS,
                        onClick = {
                            triggerHaptic()
                            onUnitChange(ValueUnit.SECONDS)
                        },
                        label = { Text("SECONDS", fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick value presets
        Text(
            text = "QUICK VALUES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        ValuePresetRow(
            unit = uiState.valueUnit,
            onPresetSelected = { presetValue ->
                triggerHaptic()
                onPresetSelected(presetValue)
            }
        )

        // Error message banner
        if (uiState.errorMessage != null && uiState.mode == ConversionDirection.VALUE_TO_TIME) {
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

        // Action buttons: Convert and Reset
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    triggerHaptic()
                    onConvert()
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
                    text = "CONVERT TO TIME",
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
        if (uiState.reverseResult != null) {
            Spacer(modifier = Modifier.height(24.dp))
            ValueToTimeResultCard(
                result = uiState.reverseResult,
                animated = uiState.settings.animations
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
