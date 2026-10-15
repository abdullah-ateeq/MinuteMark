package com.example.minutemark.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@Composable
fun TimelineBar(
    time: LocalTime,
    modifier: Modifier = Modifier,
    animated: Boolean = true
) {
    val totalSeconds = time.hour * 3600f + time.minute * 60f + time.second
    val fraction = (totalSeconds / 86400f).coerceIn(0f, 1f)

    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = if (animated) tween(durationMillis = 400) else tween(durationMillis = 0),
        label = "TimelineFraction"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val activeColor = MaterialTheme.colorScheme.primary
    val markerColor = MaterialTheme.colorScheme.primary
    val markerBorderColor = MaterialTheme.colorScheme.surface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "24-hour timeline position: ${(fraction * 100).toInt()}% of the day"
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "00:00",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "06:00",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "12:00 (Noon)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "18:00",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "24:00",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .padding(horizontal = 4.dp)
        ) {
            val barHeight = 6.dp.toPx()
            val yOffset = (size.height - barHeight) / 2
            val cornerRadius = CornerRadius(barHeight / 2, barHeight / 2)

            // Background Track
            drawRoundRect(
                color = trackColor,
                topLeft = Offset(0f, yOffset),
                size = Size(size.width, barHeight),
                cornerRadius = cornerRadius
            )

            // Progress Fill
            val progressWidth = size.width * animatedFraction
            if (progressWidth > 0) {
                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(0f, yOffset),
                    size = Size(progressWidth, barHeight),
                    cornerRadius = cornerRadius
                )
            }

            // Milestone ticks (quarter days)
            val quarterWidth = size.width / 4f
            for (i in 1..3) {
                val tickX = quarterWidth * i
                drawCircle(
                    color = trackColor.copy(alpha = 0.8f),
                    radius = 3.dp.toPx(),
                    center = Offset(tickX, size.height / 2)
                )
            }

            // Indicator Marker
            val markerX = (size.width * animatedFraction).coerceIn(0f, size.width)
            drawCircle(
                color = markerBorderColor,
                radius = 8.dp.toPx(),
                center = Offset(markerX, size.height / 2)
            )
            drawCircle(
                color = markerColor,
                radius = 6.dp.toPx(),
                center = Offset(markerX, size.height / 2)
            )
        }
    }
}
