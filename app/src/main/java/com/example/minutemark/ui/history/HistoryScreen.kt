package com.example.minutemark.ui.history

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.minutemark.data.model.ConversionDirection
import com.example.minutemark.data.model.MinuteMarkHistory
import com.example.minutemark.data.model.MinuteMarkUiState
import com.example.minutemark.domain.MinuteMarkFormatter
import com.example.minutemark.ui.viewmodel.MinuteMarkViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MinuteMarkViewModel,
    uiState: MinuteMarkUiState,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<MinuteMarkHistory?>(null) }

    fun triggerHaptic() {
        if (uiState.settings.hapticFeedback) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear History") },
            text = { Text("Clear all recent conversions?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        triggerHaptic()
                        viewModel.clearHistory()
                        showClearConfirmDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (itemToDelete != null) {
        val target = itemToDelete!!
        if (uiState.settings.confirmHistoryDelete) {
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = { Text("Delete Entry") },
                text = { Text("Delete this conversion from history?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            triggerHaptic()
                            viewModel.deleteHistoryItem(target.id)
                            itemToDelete = null
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { itemToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        } else {
            viewModel.deleteHistoryItem(target.id)
            itemToDelete = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Recent Conversions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Last ${uiState.history.size} of 10 stored conversions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    if (uiState.history.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                triggerHaptic()
                                showClearConfirmDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all history",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (uiState.history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No recent conversions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Convert a clock time or value to see your history here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = uiState.history,
                    key = { it.id }
                ) { item ->
                    HistoryItemCard(
                        item = item,
                        onItemClick = {
                            triggerHaptic()
                            viewModel.restoreHistory(item)
                            onNavigateToHome()
                        },
                        onDeleteClick = {
                            triggerHaptic()
                            itemToDelete = item
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: MinuteMarkHistory,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (item.direction == ConversionDirection.TIME_TO_VALUE)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = if (item.direction == ConversionDirection.TIME_TO_VALUE) "TIME → VALUE" else "VALUE → TIME",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (item.direction == ConversionDirection.TIME_TO_VALUE)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val timeFormatter = remember {
                        DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
                    }
                    val timestampStr = remember(item.calculatedAt) {
                        try {
                            Instant.ofEpochMilli(item.calculatedAt)
                                .atZone(ZoneId.systemDefault())
                                .format(timeFormatter)
                        } catch (e: Exception) {
                            ""
                        }
                    }
                    if (timestampStr.isNotEmpty()) {
                        Text(
                            text = timestampStr,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val inputStr = if (item.direction == ConversionDirection.TIME_TO_VALUE) {
                        item.time?.let { MinuteMarkFormatter.formatTime12Hour(it, it.second > 0) } ?: ""
                    } else {
                        "${item.value ?: 0} ${item.unit?.name?.lowercase() ?: ""}"
                    }

                    val resultStr = if (item.direction == ConversionDirection.TIME_TO_VALUE) {
                        "${MinuteMarkFormatter.formatMinutes(item.resultMinutes ?: 0)} min"
                    } else {
                        item.resultTime?.let { MinuteMarkFormatter.formatTime12Hour(it, it.second > 0) } ?: ""
                    }

                    Text(
                        text = inputStr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "converts to",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = resultStr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete item",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
