package com.example.minutemark.data.repository

import android.content.SharedPreferences
import com.example.minutemark.data.model.AppTheme
import com.example.minutemark.data.model.ConversionDirection
import com.example.minutemark.data.model.UserSettings
import com.example.minutemark.data.model.ValueUnit
import java.time.LocalTime

data class LastSavedState(
    val mode: ConversionDirection,
    val time: LocalTime,
    val includeSeconds: Boolean,
    val reverseValue: String,
    val reverseUnit: ValueUnit
)

class SettingsRepository(
    private val preferences: SharedPreferences
) {
    companion object {
        const val KEY_THEME = "pref_theme"
        const val KEY_DYNAMIC_COLOR = "pref_dynamic_color"
        const val KEY_HAPTIC_FEEDBACK = "pref_haptic_feedback"
        const val KEY_ANIMATIONS = "pref_animations"
        const val KEY_PRESERVE_LAST = "pref_preserve_last"
        const val KEY_INCLUDE_SECONDS_DEFAULT = "pref_include_seconds_default"
        const val KEY_CONFIRM_HISTORY_DELETE = "pref_confirm_history_delete"

        const val KEY_LAST_MODE = "pref_last_mode"
        const val KEY_LAST_TIME = "pref_last_time"
        const val KEY_LAST_INCLUDE_SECONDS = "pref_last_include_seconds"
        const val KEY_LAST_REVERSE_VALUE = "pref_last_reverse_value"
        const val KEY_LAST_REVERSE_UNIT = "pref_last_reverse_unit"
    }

    fun getSettings(): UserSettings {
        val themeName = preferences.getString(KEY_THEME, AppTheme.SYSTEM.name) ?: AppTheme.SYSTEM.name
        val theme = try {
            AppTheme.valueOf(themeName)
        } catch (e: Exception) {
            AppTheme.SYSTEM
        }

        return UserSettings(
            theme = theme,
            dynamicColor = preferences.getBoolean(KEY_DYNAMIC_COLOR, true),
            hapticFeedback = preferences.getBoolean(KEY_HAPTIC_FEEDBACK, true),
            animations = preferences.getBoolean(KEY_ANIMATIONS, true),
            preserveLastCalculation = preferences.getBoolean(KEY_PRESERVE_LAST, true),
            includeSecondsByDefault = preferences.getBoolean(KEY_INCLUDE_SECONDS_DEFAULT, false),
            confirmHistoryDelete = preferences.getBoolean(KEY_CONFIRM_HISTORY_DELETE, true)
        )
    }

    fun updateSettings(settings: UserSettings) {
        preferences.edit()
            .putString(KEY_THEME, settings.theme.name)
            .putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            .putBoolean(KEY_HAPTIC_FEEDBACK, settings.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, settings.animations)
            .putBoolean(KEY_PRESERVE_LAST, settings.preserveLastCalculation)
            .putBoolean(KEY_INCLUDE_SECONDS_DEFAULT, settings.includeSecondsByDefault)
            .putBoolean(KEY_CONFIRM_HISTORY_DELETE, settings.confirmHistoryDelete)
            .apply()
    }

    fun saveLastCalculationState(state: LastSavedState) {
        if (!getSettings().preserveLastCalculation) return
        preferences.edit()
            .putString(KEY_LAST_MODE, state.mode.name)
            .putString(KEY_LAST_TIME, state.time.toString())
            .putBoolean(KEY_LAST_INCLUDE_SECONDS, state.includeSeconds)
            .putString(KEY_LAST_REVERSE_VALUE, state.reverseValue)
            .putString(KEY_LAST_REVERSE_UNIT, state.reverseUnit.name)
            .apply()
    }

    fun loadLastCalculationState(): LastSavedState? {
        if (!getSettings().preserveLastCalculation) return null
        val modeStr = preferences.getString(KEY_LAST_MODE, null) ?: return null
        val timeStr = preferences.getString(KEY_LAST_TIME, null) ?: return null
        val reverseValue = preferences.getString(KEY_LAST_REVERSE_VALUE, "570") ?: "570"
        val reverseUnitStr = preferences.getString(KEY_LAST_REVERSE_UNIT, ValueUnit.MINUTES.name)
            ?: ValueUnit.MINUTES.name

        return try {
            val mode = ConversionDirection.valueOf(modeStr)
            val time = LocalTime.parse(timeStr)
            val includeSeconds = preferences.getBoolean(KEY_LAST_INCLUDE_SECONDS, false)
            val unit = ValueUnit.valueOf(reverseUnitStr)
            LastSavedState(
                mode = mode,
                time = time,
                includeSeconds = includeSeconds,
                reverseValue = reverseValue,
                reverseUnit = unit
            )
        } catch (e: Exception) {
            null
        }
    }

    fun resetSettings(): UserSettings {
        preferences.edit()
            .remove(KEY_THEME)
            .remove(KEY_DYNAMIC_COLOR)
            .remove(KEY_HAPTIC_FEEDBACK)
            .remove(KEY_ANIMATIONS)
            .remove(KEY_PRESERVE_LAST)
            .remove(KEY_INCLUDE_SECONDS_DEFAULT)
            .remove(KEY_CONFIRM_HISTORY_DELETE)
            .remove(KEY_LAST_MODE)
            .remove(KEY_LAST_TIME)
            .remove(KEY_LAST_INCLUDE_SECONDS)
            .remove(KEY_LAST_REVERSE_VALUE)
            .remove(KEY_LAST_REVERSE_UNIT)
            .apply()
        return UserSettings()
    }
}
