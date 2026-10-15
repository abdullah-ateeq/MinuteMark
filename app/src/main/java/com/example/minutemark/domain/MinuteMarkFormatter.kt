package com.example.minutemark.domain

import com.example.minutemark.data.model.MinuteMarkResult
import com.example.minutemark.data.model.ReverseTimeResult
import com.example.minutemark.data.model.ValueUnit
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object MinuteMarkFormatter {

    private val numberFormat: NumberFormat
        get() = NumberFormat.getNumberInstance(Locale.US)

    private val decimalSymbols = DecimalFormatSymbols(Locale.US)

    private val decimalFormat: DecimalFormat
        get() = DecimalFormat("#,##0.0###", decimalSymbols)

    private val time12FormatterNoSec = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
    private val time12FormatterWithSec = DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.US)
    private val time24FormatterNoSec = DateTimeFormatter.ofPattern("HH:mm", Locale.US)
    private val time24FormatterWithSec = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.US)

    fun formatTime12Hour(time: LocalTime, includeSeconds: Boolean = false): String {
        return if (includeSeconds || time.second > 0) {
            time.format(time12FormatterWithSec)
        } else {
            time.format(time12FormatterNoSec)
        }
    }

    fun formatTime24Hour(time: LocalTime, includeSeconds: Boolean = false): String {
        return if (includeSeconds || time.second > 0) {
            time.format(time24FormatterWithSec)
        } else {
            time.format(time24FormatterNoSec)
        }
    }

    fun formatMinutes(totalMinutes: Long): String {
        return numberFormat.format(totalMinutes)
    }

    fun formatSeconds(totalSeconds: Long): String {
        return numberFormat.format(totalSeconds)
    }

    fun formatDecimalMinutes(totalSeconds: Long): String {
        val decimalMinutes = totalSeconds / 60.0
        return decimalFormat.format(decimalMinutes)
    }

    fun formatDecimalHours(totalSeconds: Long): String {
        val decimalHours = totalSeconds / 3600.0
        return decimalFormat.format(decimalHours)
    }

    fun formatDayOffsetBadge(daysOffset: Long): String {
        return when {
            daysOffset == 0L -> "0 DAYS OFFSET"
            daysOffset == 1L -> "1 DAY LATER"
            else -> "$daysOffset DAYS LATER"
        }
    }

    fun formatReverseResult(result: ReverseTimeResult): String {
        val timeStr = formatTime12Hour(result.time, result.seconds > 0)
        return if (result.daysOffset == 0L) {
            timeStr
        } else {
            "$timeStr (${formatDayOffsetBadge(result.daysOffset)})"
        }
    }

    fun formatMultiDayBreakdown(result: ReverseTimeResult): String {
        if (result.daysOffset == 0L) return "Same day (0 days offset)"
        val dayPart = if (result.daysOffset == 1L) "1 DAY" else "${result.daysOffset} DAYS"
        val hourPart = when (result.hours) {
            0 -> ""
            1 -> " + 1 HOUR"
            else -> " + ${result.hours} HOURS"
        }
        val minutePart = when (result.minutes) {
            0 -> ""
            1 -> " + 1 MINUTE"
            else -> " + ${result.minutes} MINUTES"
        }
        return "$dayPart$hourPart$minutePart"
    }

    fun formatHoursFromMidnight(result: ReverseTimeResult): String {
        val totalHours = when (result.inputUnit) {
            ValueUnit.MINUTES -> result.inputValue / 60L
            ValueUnit.SECONDS -> result.inputValue / 3600L
        }
        val unitStr = if (totalHours == 1L) "HOUR" else "HOURS"
        return "$totalHours $unitStr FROM MIDNIGHT"
    }

    fun formatPlural(count: Long, singular: String, plural: String): String {
        val formattedNumber = numberFormat.format(count)
        return if (count == 1L) "$formattedNumber $singular" else "$formattedNumber $plural"
    }

    fun formatCopyText(result: Any): String {
        return when (result) {
            is MinuteMarkResult -> {
                val hasSec = result.seconds > 0
                """
                MinuteMark Result

                Time: ${formatTime12Hour(result.time, hasSec)}
                24-hour: ${formatTime24Hour(result.time, hasSec)}

                Minutes since midnight: ${result.totalMinutes}
                Seconds since midnight: ${result.totalSeconds}

                Decimal hours: ${formatDecimalHours(result.totalSeconds)}
                """.trimIndent()
            }
            is ReverseTimeResult -> {
                val unitName = result.inputUnit.name.lowercase(Locale.US)
                val hasSec = result.seconds > 0
                """
                MinuteMark Result

                Input: ${result.inputValue} $unitName

                Time: ${formatTime12Hour(result.time, hasSec)}
                24-hour: ${formatTime24Hour(result.time, hasSec)}

                Day offset: ${result.daysOffset}
                """.trimIndent()
            }
            else -> result.toString()
        }
    }

    fun formatShareText(result: Any): String {
        return when (result) {
            is MinuteMarkResult -> {
                val hasSec = result.seconds > 0
                """
                MinuteMark

                ${formatTime12Hour(result.time, hasSec)}

                ${formatMinutes(result.totalMinutes)} minutes since midnight
                ${formatSeconds(result.totalSeconds)} seconds since midnight

                ${formatDecimalHours(result.totalSeconds)} hours
                """.trimIndent()
            }
            is ReverseTimeResult -> {
                val unitName = result.inputUnit.name.lowercase(Locale.US)
                val hasSec = result.seconds > 0
                val dayOffsetStr = if (result.daysOffset > 0) "\nDay offset: ${result.daysOffset} (${formatDayOffsetBadge(result.daysOffset)})" else ""
                """
                MinuteMark

                Input: ${result.inputValue} $unitName

                Result: ${formatTime12Hour(result.time, hasSec)} (24h: ${formatTime24Hour(result.time, hasSec)})$dayOffsetStr
                """.trimIndent()
            }
            else -> result.toString()
        }
    }
}
