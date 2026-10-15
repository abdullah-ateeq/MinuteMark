package com.example.minutemark

import com.example.minutemark.data.model.AppTheme
import com.example.minutemark.data.model.ConversionDirection
import com.example.minutemark.data.model.MinuteMarkHistory
import com.example.minutemark.data.model.UserSettings
import com.example.minutemark.data.model.ValueUnit
import com.example.minutemark.data.repository.LastSavedState
import com.example.minutemark.data.repository.MinuteMarkHistoryRepository
import com.example.minutemark.data.repository.SettingsRepository
import com.example.minutemark.domain.MinuteMarkCalculator
import com.example.minutemark.domain.MinuteMarkFormatter
import com.example.minutemark.domain.MinuteMarkValidator
import com.example.minutemark.domain.ValidationResult
import com.example.minutemark.testutil.InMemorySharedPreferences
import com.example.minutemark.ui.viewmodel.MinuteMarkViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalTime
import java.util.UUID

class MinuteMarkTestSuite {

    private lateinit var prefs: InMemorySharedPreferences
    private lateinit var historyRepository: MinuteMarkHistoryRepository
    private lateinit var settingsRepository: SettingsRepository

    @Before
    fun setUp() {
        prefs = InMemorySharedPreferences()
        historyRepository = MinuteMarkHistoryRepository(prefs)
        settingsRepository = SettingsRepository(prefs)
    }

    // 1. Midnight
    @Test
    fun test01_midnight() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.MIDNIGHT)
        assertEquals(0, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(0, result.seconds)
        assertEquals(0L, result.totalMinutes)
        assertEquals(0L, result.totalSeconds)
        assertEquals(0.0, result.decimalHours, 0.0001)
        assertEquals(0.0, result.decimalMinutes, 0.0001)
    }

    // 2. 1 AM
    @Test
    fun test02_oneAm() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(1, 0))
        assertEquals(1, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(60L, result.totalMinutes)
        assertEquals(3600L, result.totalSeconds)
    }

    // 3. 6 AM
    @Test
    fun test03_sixAm() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(6, 0))
        assertEquals(6, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(360L, result.totalMinutes)
        assertEquals(21600L, result.totalSeconds)
    }

    // 4. 9 AM
    @Test
    fun test04_nineAm() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(9, 0))
        assertEquals(9, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(540L, result.totalMinutes)
        assertEquals(32400L, result.totalSeconds)
    }

    // 5. Noon
    @Test
    fun test05_noon() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.NOON)
        assertEquals(12, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(720L, result.totalMinutes)
        assertEquals(43200L, result.totalSeconds)
    }

    // 6. 3 PM
    @Test
    fun test06_threePm() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(15, 0))
        assertEquals(15, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(900L, result.totalMinutes)
        assertEquals(54000L, result.totalSeconds)
    }

    // 7. 6 PM
    @Test
    fun test07_sixPm() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(18, 0))
        assertEquals(18, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(1080L, result.totalMinutes)
        assertEquals(64800L, result.totalSeconds)
    }

    // 8. 9 PM
    @Test
    fun test08_ninePm() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(21, 0))
        assertEquals(21, result.hours)
        assertEquals(0, result.minutes)
        assertEquals(1260L, result.totalMinutes)
        assertEquals(75600L, result.totalSeconds)
    }

    // 9. 11:59 PM
    @Test
    fun test09_elevenFiftyNinePm() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(23, 59))
        assertEquals(23, result.hours)
        assertEquals(59, result.minutes)
        assertEquals(1439L, result.totalMinutes)
        assertEquals(86340L, result.totalSeconds)
    }

    // 10. 11:59:59 PM
    @Test
    fun test10_elevenFiftyNineFiftyNinePm() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(23, 59, 59))
        assertEquals(23, result.hours)
        assertEquals(59, result.minutes)
        assertEquals(59, result.seconds)
        assertEquals(1439L, result.totalMinutes)
        assertEquals(86399L, result.totalSeconds)
        assertTrue(result.totalSeconds < 86400L)
        assertEquals(86399.0 / 60.0, result.decimalMinutes, 0.0001)
        assertEquals(86399.0 / 3600.0, result.decimalHours, 0.0001)
    }

    // 11. 12:00 AM (Critical test)
    @Test
    fun test11_criticalMidnight() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(0, 0, 0))
        assertEquals(0L, result.totalMinutes)
        assertEquals(0L, result.totalSeconds)
    }

    // 12. 12:00 PM (Critical test)
    @Test
    fun test12_criticalNoon() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(12, 0, 0))
        assertEquals(720L, result.totalMinutes)
        assertEquals(43200L, result.totalSeconds)
    }

    // 13. Total minutes calculation
    @Test
    fun test13_totalMinutes() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30))
        assertEquals(570L, result.totalMinutes)
    }

    // 14. Total seconds calculation
    @Test
    fun test14_totalSeconds() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30))
        assertEquals(34200L, result.totalSeconds)
    }

    // 15. Decimal minutes calculation
    @Test
    fun test15_decimalMinutes() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30, 30))
        assertEquals(570L, result.totalMinutes)
        assertEquals(34230L, result.totalSeconds)
        assertEquals(570.5, result.decimalMinutes, 0.0001)
    }

    // 16. Decimal hours calculation
    @Test
    fun test16_decimalHours() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30, 0))
        assertEquals(9.5, result.decimalHours, 0.0001)

        val resultSec = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30, 30))
        assertEquals(34230.0 / 3600.0, resultSec.decimalHours, 0.0001)
    }

    // 17. Seconds enabled
    @Test
    fun test17_secondsEnabled() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30, 45))
        assertEquals(34245L, result.totalSeconds)
        assertEquals(570L, result.totalMinutes)
        assertEquals(570.75, result.decimalMinutes, 0.0001)
    }

    // 18. Seconds disabled (seconds = 0)
    @Test
    fun test18_secondsDisabled() {
        val result = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30, 0))
        assertEquals(0, result.seconds)
        assertEquals(34200L, result.totalSeconds)
        assertEquals(570L, result.totalMinutes)
    }

    // 19. 570 minutes → 09:30 AM
    @Test
    fun test19_reverse570Minutes() {
        val result = MinuteMarkCalculator.fromMinutes(570L)
        assertEquals(LocalTime.of(9, 30), result.time)
        assertEquals(0L, result.daysOffset)
        assertFalse(result.wrappedToDay)
    }

    // 20. 34200 seconds → 09:30 AM
    @Test
    fun test20_reverse34200Seconds() {
        val result = MinuteMarkCalculator.fromSeconds(34200L)
        assertEquals(LocalTime.of(9, 30, 0), result.time)
        assertEquals(0L, result.daysOffset)
        assertFalse(result.wrappedToDay)
    }

    // 21. 1440 minutes → midnight
    @Test
    fun test21_reverse1440Minutes() {
        val result = MinuteMarkCalculator.fromMinutes(1440L)
        assertEquals(LocalTime.of(0, 0), result.time)
        assertEquals(1L, result.daysOffset)
        assertTrue(result.wrappedToDay)
    }

    // 22. 86400 seconds → midnight
    @Test
    fun test22_reverse86400Seconds() {
        val result = MinuteMarkCalculator.fromSeconds(86400L)
        assertEquals(LocalTime.of(0, 0, 0), result.time)
        assertEquals(1L, result.daysOffset)
        assertTrue(result.wrappedToDay)
    }

    // 23. 1500 minutes → 01:00 AM
    @Test
    fun test23_reverse1500Minutes() {
        val result = MinuteMarkCalculator.fromMinutes(1500L)
        assertEquals(LocalTime.of(1, 0), result.time)
        assertEquals(1L, result.daysOffset)
        assertTrue(result.wrappedToDay)
    }

    // 24. 90000 seconds → 01:00 AM
    @Test
    fun test24_reverse90000Seconds() {
        val result = MinuteMarkCalculator.fromSeconds(90000L)
        assertEquals(LocalTime.of(1, 0, 0), result.time)
        assertEquals(1L, result.daysOffset)
        assertTrue(result.wrappedToDay)
    }

    // 25. 2880 minutes → midnight
    @Test
    fun test25_reverse2880Minutes() {
        val result = MinuteMarkCalculator.fromMinutes(2880L)
        assertEquals(LocalTime.of(0, 0), result.time)
        assertEquals(2L, result.daysOffset)
        assertTrue(result.wrappedToDay)
    }

    // 26. 172800 seconds → midnight
    @Test
    fun test26_reverse172800Seconds() {
        val result = MinuteMarkCalculator.fromSeconds(172800L)
        assertEquals(LocalTime.of(0, 0, 0), result.time)
        assertEquals(2L, result.daysOffset)
        assertTrue(result.wrappedToDay)
    }

    // 27. Large values
    @Test
    fun test27_largeValues() {
        val safeSeconds = 1_000_000_000L
        val result = MinuteMarkCalculator.fromSeconds(safeSeconds)
        assertNotNull(result.time)
        assertEquals(safeSeconds / 86400L, result.daysOffset)
    }

    // 28. Zero
    @Test
    fun test28_zeroMinutesAndSeconds() {
        val minResult = MinuteMarkCalculator.fromMinutes(0L)
        assertEquals(LocalTime.of(0, 0), minResult.time)
        assertEquals(0L, minResult.daysOffset)
        assertFalse(minResult.wrappedToDay)

        val secResult = MinuteMarkCalculator.fromSeconds(0L)
        assertEquals(LocalTime.of(0, 0, 0), secResult.time)
        assertEquals(0L, secResult.daysOffset)
        assertFalse(secResult.wrappedToDay)
    }

    // 29. Negative values validation
    @Test
    fun test29_negativeValues() {
        val valResult = MinuteMarkValidator.validateReverseValue("-10", ValueUnit.MINUTES)
        assertTrue(valResult is ValidationResult.Invalid)
        assertEquals("Enter a non-negative whole number.", (valResult as ValidationResult.Invalid).message)
    }

    // 30. Decimal values validation
    @Test
    fun test30_decimalValues() {
        val valResult = MinuteMarkValidator.validateReverseValue("570.5", ValueUnit.MINUTES)
        assertTrue(valResult is ValidationResult.Invalid)
        assertEquals("Enter a non-negative whole number.", (valResult as ValidationResult.Invalid).message)
    }

    // 31. Overflow protection
    @Test
    fun test31_overflowProtection() {
        val overflowInput = "1000000001" // exceeds 1,000,000,000 limit
        val valResult = MinuteMarkValidator.validateReverseValue(overflowInput, ValueUnit.SECONDS)
        assertTrue(valResult is ValidationResult.Invalid)
        assertEquals("Value is too large.", (valResult as ValidationResult.Invalid).message)

        val hugeInput = "999999999999999999999999999"
        val hugeResult = MinuteMarkValidator.validateReverseValue(hugeInput, ValueUnit.SECONDS)
        assertTrue(hugeResult is ValidationResult.Invalid)
        assertEquals("Value is too large.", (hugeResult as ValidationResult.Invalid).message)
    }

    // 32. Day offset
    @Test
    fun test32_dayOffset() {
        assertEquals(0L, MinuteMarkCalculator.fromMinutes(570L).daysOffset)
        assertEquals(1L, MinuteMarkCalculator.fromMinutes(1440L).daysOffset)
        assertEquals(1L, MinuteMarkCalculator.fromMinutes(1500L).daysOffset)
        assertEquals(2L, MinuteMarkCalculator.fromMinutes(2880L).daysOffset)
        assertEquals(3L, MinuteMarkCalculator.fromMinutes(4320L).daysOffset)
    }

    // 33. Minute wrapping
    @Test
    fun test33_minuteWrapping() {
        val result = MinuteMarkCalculator.fromMinutes(1500L)
        // 1500 % 1440 = 60 minutes = 1:00 AM
        assertEquals(1, result.time.hour)
        assertEquals(0, result.time.minute)
    }

    // 34. Second wrapping
    @Test
    fun test34_secondWrapping() {
        val result = MinuteMarkCalculator.fromSeconds(90000L)
        // 90000 % 86400 = 3600 seconds = 1:00:00 AM
        assertEquals(1, result.time.hour)
        assertEquals(0, result.time.minute)
        assertEquals(0, result.time.second)
    }

    // 35. Singular/plural formatting
    @Test
    fun test35_singularPluralFormatting() {
        assertEquals("1 day", MinuteMarkFormatter.formatPlural(1L, "day", "days"))
        assertEquals("2 days", MinuteMarkFormatter.formatPlural(2L, "day", "days"))
        assertEquals("1 minute", MinuteMarkFormatter.formatPlural(1L, "minute", "minutes"))
        assertEquals("570 minutes", MinuteMarkFormatter.formatPlural(570L, "minute", "minutes"))
        assertEquals("1 second", MinuteMarkFormatter.formatPlural(1L, "second", "seconds"))
        assertEquals("34,200 seconds", MinuteMarkFormatter.formatPlural(34200L, "second", "seconds"))
    }

    // 36. History save
    @Test
    fun test36_historySave() {
        val item = MinuteMarkHistory(
            id = UUID.randomUUID().toString(),
            direction = ConversionDirection.TIME_TO_VALUE,
            time = LocalTime.of(9, 30),
            value = null,
            unit = null,
            resultMinutes = 570L,
            resultSeconds = 34200L,
            resultTime = null,
            calculatedAt = System.currentTimeMillis()
        )
        val history = historyRepository.addHistory(item)
        assertEquals(1, history.size)
        assertEquals(item.id, history.first().id)
    }

    // 37. History load
    @Test
    fun test37_historyLoad() {
        val item = MinuteMarkHistory(
            id = "test-1",
            direction = ConversionDirection.VALUE_TO_TIME,
            time = null,
            value = 570L,
            unit = ValueUnit.MINUTES,
            resultMinutes = null,
            resultSeconds = null,
            resultTime = LocalTime.of(9, 30),
            calculatedAt = System.currentTimeMillis()
        )
        historyRepository.addHistory(item)
        val loaded = historyRepository.getHistory()
        assertEquals(1, loaded.size)
        assertEquals("test-1", loaded[0].id)
        assertEquals(ValueUnit.MINUTES, loaded[0].unit)
        assertEquals(LocalTime.of(9, 30), loaded[0].resultTime)
    }

    // 38. History limit (max 10 entries)
    @Test
    fun test38_historyLimit() {
        for (i in 1..15) {
            val item = MinuteMarkHistory(
                id = "item-$i",
                direction = ConversionDirection.TIME_TO_VALUE,
                time = LocalTime.of((i % 24), 0),
                value = null,
                unit = null,
                resultMinutes = (i * 60).toLong(),
                resultSeconds = (i * 3600).toLong(),
                resultTime = null,
                calculatedAt = System.currentTimeMillis() + i
            )
            historyRepository.addHistory(item)
        }
        val history = historyRepository.getHistory()
        assertEquals(10, history.size)
        // Most recent should be at the top
        assertEquals("item-15", history.first().id)
    }

    // 39. History deletion
    @Test
    fun test39_historyDeletion() {
        val item1 = MinuteMarkHistory("id-1", ConversionDirection.TIME_TO_VALUE, LocalTime.of(9, 0), null, null, 540L, 32400L, null, 100L)
        val item2 = MinuteMarkHistory("id-2", ConversionDirection.TIME_TO_VALUE, LocalTime.of(10, 0), null, null, 600L, 36000L, null, 200L)
        historyRepository.addHistory(item1)
        historyRepository.addHistory(item2)

        val updated = historyRepository.deleteHistoryItem("id-1")
        assertEquals(1, updated.size)
        assertEquals("id-2", updated[0].id)
    }

    // 40. History clear
    @Test
    fun test40_historyClear() {
        val item = MinuteMarkHistory("id-1", ConversionDirection.TIME_TO_VALUE, LocalTime.of(9, 0), null, null, 540L, 32400L, null, 100L)
        historyRepository.addHistory(item)
        assertEquals(1, historyRepository.getHistory().size)

        historyRepository.clearHistory()
        assertEquals(0, historyRepository.getHistory().size)
    }

    // 41. Settings persistence
    @Test
    fun test41_settingsPersistence() {
        val newSettings = UserSettings(
            theme = AppTheme.DARK,
            dynamicColor = false,
            hapticFeedback = false,
            animations = true,
            preserveLastCalculation = false,
            includeSecondsByDefault = true,
            confirmHistoryDelete = false
        )
        settingsRepository.updateSettings(newSettings)
        val loaded = settingsRepository.getSettings()

        assertEquals(AppTheme.DARK, loaded.theme)
        assertFalse(loaded.dynamicColor)
        assertFalse(loaded.hapticFeedback)
        assertTrue(loaded.animations)
        assertFalse(loaded.preserveLastCalculation)
        assertTrue(loaded.includeSecondsByDefault)
        assertFalse(loaded.confirmHistoryDelete)
    }

    // 42. Last-calculation persistence
    @Test
    fun test42_lastCalculationPersistence() {
        val state = LastSavedState(
            mode = ConversionDirection.VALUE_TO_TIME,
            time = LocalTime.of(10, 15, 30),
            includeSeconds = true,
            reverseValue = "1500",
            reverseUnit = ValueUnit.MINUTES
        )
        settingsRepository.saveLastCalculationState(state)
        val loaded = settingsRepository.loadLastCalculationState()

        assertNotNull(loaded)
        assertEquals(ConversionDirection.VALUE_TO_TIME, loaded?.mode)
        assertEquals(LocalTime.of(10, 15, 30), loaded?.time)
        assertTrue(loaded?.includeSeconds == true)
        assertEquals("1500", loaded?.reverseValue)
        assertEquals(ValueUnit.MINUTES, loaded?.reverseUnit)
    }

    // 43. Reset
    @Test
    fun test43_reset() {
        val viewModel = MinuteMarkViewModel(historyRepository, settingsRepository)
        viewModel.setTime(14, 25)
        viewModel.setIncludeSeconds(true)
        viewModel.setSecondsInput("45")
        viewModel.calculate()

        assertNotNull(viewModel.uiState.value.result)

        viewModel.reset()

        val state = viewModel.uiState.value
        assertEquals(MinuteMarkViewModel.DEFAULT_TIME, state.selectedTime)
        assertFalse(state.includeSeconds)
        assertEquals("0", state.secondsInput)
        assertNull(state.result)
    }

    // 44. Copy formatting
    @Test
    fun test44_copyFormatting() {
        val timeResult = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30, 0))
        val copyText = MinuteMarkFormatter.formatCopyText(timeResult)
        assertTrue(copyText.contains("Time: 09:30 AM"))
        assertTrue(copyText.contains("24-hour: 09:30"))
        assertTrue(copyText.contains("Minutes since midnight: 570"))
        assertTrue(copyText.contains("Seconds since midnight: 34200"))
    }

    // 45. Share formatting
    @Test
    fun test45_shareFormatting() {
        val timeResult = MinuteMarkCalculator.fromTime(LocalTime.of(9, 30, 0))
        val shareText = MinuteMarkFormatter.formatShareText(timeResult)
        assertTrue(shareText.contains("MinuteMark"))
        assertTrue(shareText.contains("09:30 AM"))
        assertTrue(shareText.contains("570 minutes since midnight"))
        assertTrue(shareText.contains("34,200 seconds since midnight"))
    }

    // 46. Quick presets
    @Test
    fun test46_quickPresets() {
        val viewModel = MinuteMarkViewModel(historyRepository, settingsRepository)
        viewModel.applyTimePreset(LocalTime.NOON)
        assertEquals(12, viewModel.uiState.value.selectedTime.hour)
        assertEquals(0, viewModel.uiState.value.selectedTime.minute)

        viewModel.setMode(ConversionDirection.VALUE_TO_TIME)
        viewModel.applyValuePreset(720L)
        assertEquals("720", viewModel.uiState.value.valueInput)
    }

    // 47. Mode switching
    @Test
    fun test47_modeSwitching() {
        val viewModel = MinuteMarkViewModel(historyRepository, settingsRepository)
        viewModel.calculate()
        assertNotNull(viewModel.uiState.value.result)

        // Switching mode clears result
        viewModel.setMode(ConversionDirection.VALUE_TO_TIME)
        assertNull(viewModel.uiState.value.result)
        assertNull(viewModel.uiState.value.reverseResult)
        assertEquals(ConversionDirection.VALUE_TO_TIME, viewModel.uiState.value.mode)
    }

    // 48. Unit switching
    @Test
    fun test48_unitSwitching() {
        val viewModel = MinuteMarkViewModel(historyRepository, settingsRepository)
        viewModel.setMode(ConversionDirection.VALUE_TO_TIME)
        viewModel.setValueUnit(ValueUnit.SECONDS)
        assertEquals(ValueUnit.SECONDS, viewModel.uiState.value.valueUnit)
    }

    // 49. Seconds validation
    @Test
    fun test49_secondsValidation() {
        assertTrue(MinuteMarkValidator.validateSeconds("0") is ValidationResult.Valid)
        assertTrue(MinuteMarkValidator.validateSeconds("59") is ValidationResult.Valid)

        val negativeResult = MinuteMarkValidator.validateSeconds("-1")
        assertTrue(negativeResult is ValidationResult.Invalid)

        val outOfBounds = MinuteMarkValidator.validateSeconds("60")
        assertTrue(outOfBounds is ValidationResult.Invalid)
        assertEquals("Seconds must be between 0 and 59.", (outOfBounds as ValidationResult.Invalid).message)

        val decimalResult = MinuteMarkValidator.validateSeconds("30.5")
        assertTrue(decimalResult is ValidationResult.Invalid)
    }

    // 50. Rapid calculation protection
    @Test
    fun test50_rapidCalculationProtection() {
        val viewModel = MinuteMarkViewModel(historyRepository, settingsRepository)
        viewModel.calculate()
        viewModel.calculate()

        // Deduplication should ensure only 1 item is saved for identical calculation
        val history = viewModel.uiState.value.history
        assertEquals(1, history.size)
    }
}
