package com.example.minutemark.data.model

import java.time.LocalTime

data class MinuteMarkUiState(
    val mode: ConversionDirection = ConversionDirection.TIME_TO_VALUE,
    val selectedTime: LocalTime = LocalTime.of(9, 30, 0),
    val includeSeconds: Boolean = false,
    val secondsInput: String = "0",
    val valueInput: String = "570",
    val valueUnit: ValueUnit = ValueUnit.MINUTES,
    val result: MinuteMarkResult? = null,
    val reverseResult: ReverseTimeResult? = null,
    val history: List<MinuteMarkHistory> = emptyList(),
    val errorMessage: String? = null,
    val isCalculating: Boolean = false,
    val calculationState: CalculationState = CalculationState.Idle,
    val settings: UserSettings = UserSettings()
)
