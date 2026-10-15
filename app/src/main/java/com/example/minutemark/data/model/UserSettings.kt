package com.example.minutemark.data.model

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserSettings(
    val theme: AppTheme = AppTheme.SYSTEM,
    val dynamicColor: Boolean = true,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val preserveLastCalculation: Boolean = true,
    val includeSecondsByDefault: Boolean = false,
    val confirmHistoryDelete: Boolean = true
)
