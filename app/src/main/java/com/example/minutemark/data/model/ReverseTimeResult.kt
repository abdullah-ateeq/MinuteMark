package com.example.minutemark.data.model

import java.time.LocalTime

data class ReverseTimeResult(
    val inputValue: Long,
    val inputUnit: ValueUnit,
    val time: LocalTime,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val normalizedTotalSeconds: Long,
    val wrappedToDay: Boolean,
    val daysOffset: Long
)
