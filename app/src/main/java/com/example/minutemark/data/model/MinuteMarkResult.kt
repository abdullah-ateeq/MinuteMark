package com.example.minutemark.data.model

import java.time.LocalTime

data class MinuteMarkResult(
    val time: LocalTime,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val totalMinutes: Long,
    val totalSeconds: Long,
    val decimalHours: Double,
    val decimalMinutes: Double
)
