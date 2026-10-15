package com.example.minutemark.data.model

import java.time.LocalTime

data class MinuteMarkHistory(
    val id: String,
    val direction: ConversionDirection,
    val time: LocalTime?,
    val value: Long?,
    val unit: ValueUnit?,
    val resultMinutes: Long?,
    val resultSeconds: Long?,
    val resultTime: LocalTime?,
    val calculatedAt: Long
)
