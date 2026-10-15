package com.example.minutemark.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.minutemark.data.model.ValueUnit
import java.time.LocalTime

data class TimePreset(val label: String, val time: LocalTime)

val DEFAULT_TIME_PRESETS = listOf(
    TimePreset("Midnight", LocalTime.of(0, 0)),
    TimePreset("6 AM", LocalTime.of(6, 0)),
    TimePreset("9 AM", LocalTime.of(9, 0)),
    TimePreset("Noon", LocalTime.of(12, 0)),
    TimePreset("3 PM", LocalTime.of(15, 0)),
    TimePreset("6 PM", LocalTime.of(18, 0)),
    TimePreset("9 PM", LocalTime.of(21, 0))
)

data class ValuePreset(val label: String, val value: Long)

val MINUTE_PRESETS = listOf(
    ValuePreset("60m", 60L),
    ValuePreset("120m", 120L),
    ValuePreset("360m", 360L),
    ValuePreset("480m", 480L),
    ValuePreset("720m", 720L),
    ValuePreset("1440m", 1440L)
)

val SECOND_PRESETS = listOf(
    ValuePreset("60s", 60L),
    ValuePreset("300s", 300L),
    ValuePreset("3600s", 3600L),
    ValuePreset("43200s", 43200L),
    ValuePreset("86400s", 86400L)
)

@Composable
fun TimePresetRow(
    onPresetSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DEFAULT_TIME_PRESETS.forEach { preset ->
            AssistChip(
                onClick = { onPresetSelected(preset.time) },
                label = {
                    Text(
                        text = preset.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun ValuePresetRow(
    unit: ValueUnit,
    onPresetSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = if (unit == ValueUnit.MINUTES) MINUTE_PRESETS else SECOND_PRESETS
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { preset ->
            AssistChip(
                onClick = { onPresetSelected(preset.value) },
                label = {
                    Text(
                        text = preset.label,
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
