package com.example.minutemark.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minutemark.data.model.MinuteMarkResult
import com.example.minutemark.data.model.ReverseTimeResult
import com.example.minutemark.domain.MinuteMarkFormatter

@Composable
fun TimeToValueResultCard(
    result: MinuteMarkResult,
    animated: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val totalMinutesFormatted = MinuteMarkFormatter.formatMinutes(result.totalMinutes)
    val totalSecondsFormatted = MinuteMarkFormatter.formatSeconds(result.totalSeconds)
    val time12 = MinuteMarkFormatter.formatTime12Hour(result.time, result.seconds > 0)
    val time24 = MinuteMarkFormatter.formatTime24Hour(result.time, result.seconds > 0)
    val decimalMinutesFormatted = MinuteMarkFormatter.formatDecimalMinutes(result.totalSeconds)
    val decimalHoursFormatted = MinuteMarkFormatter.formatDecimalHours(result.totalSeconds)

    val a11yDescription = "$totalMinutesFormatted minutes since midnight and $totalSecondsFormatted seconds since midnight"

    AnimatedVisibility(
        visible = true,
        enter = if (animated) fadeIn() + slideInVertically { it / 4 } else fadeIn()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .semantics { contentDescription = a11yDescription },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "TIME → VALUE RESULT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = time12,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary result: MINUTES SINCE MIDNIGHT
                Text(
                    text = totalMinutesFormatted,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "MINUTES SINCE MIDNIGHT",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Secondary result: SECONDS SINCE MIDNIGHT
                Text(
                    text = totalSecondsFormatted,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "SECONDS SINCE MIDNIGHT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // 24-hour visual timeline
                Text(
                    text = "Position in Day",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimelineBar(time = result.time, animated = animated)

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Result Details grid
                Text(
                    text = "Detailed Breakdown",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                ResultDetailRow(label = "Clock time", value = time12)
                ResultDetailRow(label = "24-hour format", value = time24)
                ResultDetailRow(label = "Hours component", value = "${result.hours}")
                ResultDetailRow(label = "Minutes component", value = "${result.minutes}")
                if (result.seconds > 0) {
                    ResultDetailRow(label = "Seconds component", value = "${result.seconds}")
                }
                ResultDetailRow(label = "Total whole minutes", value = "$totalMinutesFormatted min")
                if (result.seconds > 0) {
                    ResultDetailRow(label = "Precise decimal minutes", value = "$decimalMinutesFormatted min")
                }
                ResultDetailRow(label = "Total seconds", value = "$totalSecondsFormatted sec")
                ResultDetailRow(label = "Decimal hours", value = "$decimalHoursFormatted hrs")

                Spacer(modifier = Modifier.height(20.dp))

                // Copy and Share actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("MinuteMark Result", MinuteMarkFormatter.formatCopyText(result))
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy result",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy")
                    }

                    FilledTonalButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "MinuteMark Result")
                                putExtra(Intent.EXTRA_TEXT, MinuteMarkFormatter.formatShareText(result))
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Result"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share result",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                }
            }
        }
    }
}

@Composable
fun ValueToTimeResultCard(
    result: ReverseTimeResult,
    animated: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val time12 = MinuteMarkFormatter.formatTime12Hour(result.time, result.seconds > 0)
    val time24 = MinuteMarkFormatter.formatTime24Hour(result.time, result.seconds > 0)
    val inputFormatted = MinuteMarkFormatter.formatSeconds(result.inputValue)
    val unitLabel = result.inputUnit.name.lowercase()
    val dayOffsetBadge = MinuteMarkFormatter.formatDayOffsetBadge(result.daysOffset)

    val a11yDescription = "Result clock time $time12, $dayOffsetBadge"

    AnimatedVisibility(
        visible = true,
        enter = if (animated) fadeIn() + slideInVertically { it / 4 } else fadeIn()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .semantics { contentDescription = a11yDescription },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "VALUE → TIME RESULT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Day offset chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (result.daysOffset > 0) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                    ) {
                        Text(
                            text = dayOffsetBadge,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (result.daysOffset > 0) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Primary result: Clock time
                Text(
                    text = time12,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "CLOCK TIME",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$inputFormatted $unitLabel",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "INPUT CONVERTED FROM MIDNIGHT",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    letterSpacing = 0.5.sp
                )

                if (result.daysOffset > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = MinuteMarkFormatter.formatHoursFromMidnight(result),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = MinuteMarkFormatter.formatMultiDayBreakdown(result),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Timeline visual for the time position
                Text(
                    text = "Position in 24-Hour Cycle",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                TimelineBar(time = result.time, animated = animated)

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Reverse Details grid
                Text(
                    text = "Conversion Details",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                ResultDetailRow(label = "Input value", value = "$inputFormatted $unitLabel")
                ResultDetailRow(label = "12-hour clock time", value = time12)
                ResultDetailRow(label = "24-hour clock time", value = time24)
                ResultDetailRow(label = "Day offset", value = "${result.daysOffset}")
                ResultDetailRow(label = "Normalized seconds in day", value = "${result.normalizedTotalSeconds} sec")

                Spacer(modifier = Modifier.height(20.dp))

                // Copy and Share actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("MinuteMark Result", MinuteMarkFormatter.formatCopyText(result))
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Result copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy result",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy")
                    }

                    FilledTonalButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "MinuteMark Result")
                                putExtra(Intent.EXTRA_TEXT, MinuteMarkFormatter.formatShareText(result))
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Result"))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share result",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultDetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
