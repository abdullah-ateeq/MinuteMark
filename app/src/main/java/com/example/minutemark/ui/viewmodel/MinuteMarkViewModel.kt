package com.example.minutemark.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.minutemark.data.model.CalculationState
import com.example.minutemark.data.model.ConversionDirection
import com.example.minutemark.data.model.MinuteMarkHistory
import com.example.minutemark.data.model.MinuteMarkUiState
import com.example.minutemark.data.model.UserSettings
import com.example.minutemark.data.model.ValueUnit
import com.example.minutemark.data.repository.LastSavedState
import com.example.minutemark.data.repository.MinuteMarkHistoryRepository
import com.example.minutemark.data.repository.SettingsRepository
import com.example.minutemark.domain.MinuteMarkCalculator
import com.example.minutemark.domain.MinuteMarkValidator
import com.example.minutemark.domain.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.util.UUID

class MinuteMarkViewModel(
    private val historyRepository: MinuteMarkHistoryRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    companion object {
        val DEFAULT_TIME: LocalTime = LocalTime.of(9, 30, 0)
        const val DEFAULT_VALUE: String = "570"
        val DEFAULT_UNIT: ValueUnit = ValueUnit.MINUTES

        fun provideFactory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val prefs = context.getSharedPreferences("minute_mark_prefs", Context.MODE_PRIVATE)
                    val historyRepo = MinuteMarkHistoryRepository(prefs)
                    val settingsRepo = SettingsRepository(prefs)
                    return MinuteMarkViewModel(historyRepo, settingsRepo) as T
                }
            }
    }

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<MinuteMarkUiState> = _uiState.asStateFlow()

    private fun createInitialState(): MinuteMarkUiState {
        val settings = settingsRepository.getSettings()
        val history = historyRepository.getHistory()
        val lastState = if (settings.preserveLastCalculation) {
            settingsRepository.loadLastCalculationState()
        } else {
            null
        }

        return if (lastState != null) {
            MinuteMarkUiState(
                mode = lastState.mode,
                selectedTime = lastState.time,
                includeSeconds = lastState.includeSeconds,
                secondsInput = lastState.time.second.toString(),
                valueInput = lastState.reverseValue,
                valueUnit = lastState.reverseUnit,
                result = null,
                reverseResult = null,
                history = history,
                errorMessage = null,
                isCalculating = false,
                calculationState = CalculationState.Idle,
                settings = settings
            )
        } else {
            MinuteMarkUiState(
                mode = ConversionDirection.TIME_TO_VALUE,
                selectedTime = DEFAULT_TIME,
                includeSeconds = settings.includeSecondsByDefault,
                secondsInput = "0",
                valueInput = DEFAULT_VALUE,
                valueUnit = DEFAULT_UNIT,
                result = null,
                reverseResult = null,
                history = history,
                errorMessage = null,
                isCalculating = false,
                calculationState = CalculationState.Idle,
                settings = settings
            )
        }
    }

    fun setMode(newMode: ConversionDirection) {
        if (_uiState.value.mode == newMode) return
        _uiState.update {
            it.copy(
                mode = newMode,
                result = null,
                reverseResult = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun setTime(hour: Int, minute: Int) {
        val currentSeconds = if (_uiState.value.includeSeconds) {
            _uiState.value.secondsInput.toIntOrNull()?.coerceIn(0, 59) ?: 0
        } else {
            0
        }
        val newTime = LocalTime.of(hour, minute, currentSeconds)
        _uiState.update {
            it.copy(
                selectedTime = newTime,
                result = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun setNow() {
        val now = LocalTime.now()
        val currentSeconds = if (_uiState.value.includeSeconds) now.second else 0
        val newTime = LocalTime.of(now.hour, now.minute, currentSeconds)
        _uiState.update {
            it.copy(
                selectedTime = newTime,
                secondsInput = currentSeconds.toString(),
                result = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun setIncludeSeconds(enabled: Boolean) {
        val current = _uiState.value.selectedTime
        val newTime = if (enabled) {
            val sec = _uiState.value.secondsInput.toIntOrNull()?.coerceIn(0, 59) ?: 0
            LocalTime.of(current.hour, current.minute, sec)
        } else {
            LocalTime.of(current.hour, current.minute, 0)
        }

        _uiState.update {
            it.copy(
                includeSeconds = enabled,
                selectedTime = newTime,
                result = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun setSecondsInput(seconds: String) {
        val sanitized = seconds.filter { it.isDigit() }.take(2)
        val secValue = sanitized.toIntOrNull()?.coerceIn(0, 59) ?: 0
        val current = _uiState.value.selectedTime
        val newTime = LocalTime.of(current.hour, current.minute, secValue)

        _uiState.update {
            it.copy(
                secondsInput = sanitized,
                selectedTime = newTime,
                result = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun applyTimePreset(time: LocalTime) {
        val currentSeconds = if (_uiState.value.includeSeconds) {
            _uiState.value.secondsInput.toIntOrNull()?.coerceIn(0, 59) ?: 0
        } else {
            0
        }
        val effectiveTime = LocalTime.of(time.hour, time.minute, currentSeconds)
        _uiState.update {
            it.copy(
                selectedTime = effectiveTime,
                result = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun setValueInput(value: String) {
        _uiState.update {
            it.copy(
                valueInput = value,
                reverseResult = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun setValueUnit(unit: ValueUnit) {
        if (_uiState.value.valueUnit == unit) return
        _uiState.update {
            it.copy(
                valueUnit = unit,
                reverseResult = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun applyValuePreset(value: Long) {
        _uiState.update {
            it.copy(
                valueInput = value.toString(),
                reverseResult = null,
                errorMessage = null,
                calculationState = CalculationState.Idle
            )
        }
        persistCurrentState()
    }

    fun calculate() {
        if (_uiState.value.isCalculating) return

        val state = _uiState.value
        val effectiveSeconds = if (state.includeSeconds) {
            when (val validation = MinuteMarkValidator.validateSeconds(state.secondsInput)) {
                is ValidationResult.Invalid -> {
                    _uiState.update {
                        it.copy(
                            errorMessage = validation.message,
                            calculationState = CalculationState.Error(validation.message)
                        )
                    }
                    return
                }
                is ValidationResult.Valid -> validation.value
            }
        } else {
            0
        }

        _uiState.update { it.copy(isCalculating = true) }

        try {
            val effectiveTime = LocalTime.of(
                state.selectedTime.hour,
                state.selectedTime.minute,
                effectiveSeconds
            )
            val result = MinuteMarkCalculator.fromTime(effectiveTime)

            val historyItem = MinuteMarkHistory(
                id = UUID.randomUUID().toString(),
                direction = ConversionDirection.TIME_TO_VALUE,
                time = effectiveTime,
                value = null,
                unit = null,
                resultMinutes = result.totalMinutes,
                resultSeconds = result.totalSeconds,
                resultTime = null,
                calculatedAt = System.currentTimeMillis()
            )
            val updatedHistory = historyRepository.addHistory(historyItem)

            _uiState.update {
                it.copy(
                    result = result,
                    selectedTime = effectiveTime,
                    history = updatedHistory,
                    errorMessage = null,
                    isCalculating = false,
                    calculationState = CalculationState.Calculated
                )
            }
            persistCurrentState()
        } catch (e: Exception) {
            val msg = e.message ?: "Calculation error"
            _uiState.update {
                it.copy(
                    errorMessage = msg,
                    isCalculating = false,
                    calculationState = CalculationState.Error(msg)
                )
            }
        }
    }

    fun calculateReverse() {
        if (_uiState.value.isCalculating) return

        val state = _uiState.value
        val validation = MinuteMarkValidator.validateReverseValue(state.valueInput, state.valueUnit)
        if (validation is ValidationResult.Invalid) {
            _uiState.update {
                it.copy(
                    errorMessage = validation.message,
                    calculationState = CalculationState.Error(validation.message)
                )
            }
            return
        }

        val numericValue = (validation as ValidationResult.Valid).value
        _uiState.update { it.copy(isCalculating = true) }

        try {
            val result = when (state.valueUnit) {
                ValueUnit.MINUTES -> MinuteMarkCalculator.fromMinutes(numericValue)
                ValueUnit.SECONDS -> MinuteMarkCalculator.fromSeconds(numericValue)
            }

            val historyItem = MinuteMarkHistory(
                id = UUID.randomUUID().toString(),
                direction = ConversionDirection.VALUE_TO_TIME,
                time = null,
                value = numericValue,
                unit = state.valueUnit,
                resultMinutes = null,
                resultSeconds = null,
                resultTime = result.time,
                calculatedAt = System.currentTimeMillis()
            )
            val updatedHistory = historyRepository.addHistory(historyItem)

            _uiState.update {
                it.copy(
                    reverseResult = result,
                    history = updatedHistory,
                    errorMessage = null,
                    isCalculating = false,
                    calculationState = CalculationState.Calculated
                )
            }
            persistCurrentState()
        } catch (e: Exception) {
            val msg = e.message ?: "Calculation error"
            _uiState.update {
                it.copy(
                    errorMessage = msg,
                    isCalculating = false,
                    calculationState = CalculationState.Error(msg)
                )
            }
        }
    }

    fun reset() {
        val currentState = _uiState.value
        if (currentState.mode == ConversionDirection.TIME_TO_VALUE) {
            _uiState.update {
                it.copy(
                    selectedTime = DEFAULT_TIME,
                    includeSeconds = false,
                    secondsInput = "0",
                    result = null,
                    errorMessage = null,
                    calculationState = CalculationState.Idle
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    valueInput = DEFAULT_VALUE,
                    valueUnit = DEFAULT_UNIT,
                    reverseResult = null,
                    errorMessage = null,
                    calculationState = CalculationState.Idle
                )
            }
        }
        persistCurrentState()
    }

    fun restoreHistory(item: MinuteMarkHistory) {
        when (item.direction) {
            ConversionDirection.TIME_TO_VALUE -> {
                val targetTime = item.time ?: DEFAULT_TIME
                val hasSec = targetTime.second > 0
                _uiState.update {
                    it.copy(
                        mode = ConversionDirection.TIME_TO_VALUE,
                        selectedTime = targetTime,
                        includeSeconds = hasSec,
                        secondsInput = targetTime.second.toString(),
                        result = null,
                        reverseResult = null,
                        errorMessage = null,
                        calculationState = CalculationState.Idle
                    )
                }
                calculate()
            }
            ConversionDirection.VALUE_TO_TIME -> {
                val targetValue = (item.value ?: 570L).toString()
                val targetUnit = item.unit ?: ValueUnit.MINUTES
                _uiState.update {
                    it.copy(
                        mode = ConversionDirection.VALUE_TO_TIME,
                        valueInput = targetValue,
                        valueUnit = targetUnit,
                        result = null,
                        reverseResult = null,
                        errorMessage = null,
                        calculationState = CalculationState.Idle
                    )
                }
                calculateReverse()
            }
        }
    }

    fun deleteHistoryItem(id: String) {
        val updated = historyRepository.deleteHistoryItem(id)
        _uiState.update { it.copy(history = updated) }
    }

    fun clearHistory() {
        historyRepository.clearHistory()
        _uiState.update { it.copy(history = emptyList()) }
    }

    fun updateSettings(newSettings: UserSettings) {
        settingsRepository.updateSettings(newSettings)
        _uiState.update { it.copy(settings = newSettings) }
    }

    fun resetSettings() {
        val defaultSettings = settingsRepository.resetSettings()
        _uiState.update { it.copy(settings = defaultSettings) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun persistCurrentState() {
        val state = _uiState.value
        if (state.settings.preserveLastCalculation) {
            settingsRepository.saveLastCalculationState(
                LastSavedState(
                    mode = state.mode,
                    time = state.selectedTime,
                    includeSeconds = state.includeSeconds,
                    reverseValue = state.valueInput,
                    reverseUnit = state.valueUnit
                )
            )
        }
    }
}
