package com.example.minutemark.domain

import com.example.minutemark.data.model.MinuteMarkResult
import com.example.minutemark.data.model.ReverseTimeResult
import com.example.minutemark.data.model.ValueUnit
import java.time.LocalTime

object MinuteMarkCalculator {

    const val MINUTES_PER_DAY = 1440L
    const val SECONDS_PER_DAY = 86400L
    const val SECONDS_PER_HOUR = 3600L
    const val SECONDS_PER_MINUTE = 60L
    const val MINUTES_PER_HOUR = 60L

    /**
     * Converts a clock time into total minutes and total seconds since midnight,
     * along with decimal hours and decimal minutes.
     */
    fun fromTime(time: LocalTime): MinuteMarkResult {
        val hours = time.hour
        val minutes = time.minute
        val seconds = time.second

        // Whole completed minutes
        val totalMinutes = Math.addExact(
            Math.multiplyExact(hours.toLong(), MINUTES_PER_HOUR),
            minutes.toLong()
        )

        // Exact total seconds
        val totalSeconds = Math.addExact(
            Math.multiplyExact(hours.toLong(), SECONDS_PER_HOUR),
            Math.addExact(
                Math.multiplyExact(minutes.toLong(), SECONDS_PER_MINUTE),
                seconds.toLong()
            )
        )

        val decimalHours = totalSeconds / SECONDS_PER_HOUR.toDouble()
        val decimalMinutes = totalSeconds / SECONDS_PER_MINUTE.toDouble()

        return MinuteMarkResult(
            time = time,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            totalMinutes = totalMinutes,
            totalSeconds = totalSeconds,
            decimalHours = decimalHours,
            decimalMinutes = decimalMinutes
        )
    }

    /**
     * Converts total minutes back to a clock time.
     * Supports values greater than 24 hours via day offset.
     */
    fun fromMinutes(totalMinutes: Long): ReverseTimeResult {
        require(totalMinutes >= 0) { "Enter a non-negative whole number." }

        val daysOffset = totalMinutes / MINUTES_PER_DAY
        val remainingMinutes = totalMinutes % MINUTES_PER_DAY

        val hours = (remainingMinutes / MINUTES_PER_HOUR).toInt()
        val minutes = (remainingMinutes % MINUTES_PER_HOUR).toInt()
        val seconds = 0

        val time = LocalTime.of(hours, minutes, seconds)
        val normalizedTotalSeconds = Math.addExact(
            Math.multiplyExact(hours.toLong(), SECONDS_PER_HOUR),
            Math.multiplyExact(minutes.toLong(), SECONDS_PER_MINUTE)
        )

        return ReverseTimeResult(
            inputValue = totalMinutes,
            inputUnit = ValueUnit.MINUTES,
            time = time,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            normalizedTotalSeconds = normalizedTotalSeconds,
            wrappedToDay = daysOffset > 0,
            daysOffset = daysOffset
        )
    }

    /**
     * Converts total seconds back to a clock time.
     * Supports values greater than 24 hours via day offset.
     */
    fun fromSeconds(totalSeconds: Long): ReverseTimeResult {
        require(totalSeconds >= 0) { "Enter a non-negative whole number." }

        val daysOffset = totalSeconds / SECONDS_PER_DAY
        val remainingSeconds = totalSeconds % SECONDS_PER_DAY

        val hours = (remainingSeconds / SECONDS_PER_HOUR).toInt()
        val minutes = ((remainingSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE).toInt()
        val seconds = (remainingSeconds % SECONDS_PER_MINUTE).toInt()

        val time = LocalTime.of(hours, minutes, seconds)
        val normalizedTotalSeconds = remainingSeconds

        return ReverseTimeResult(
            inputValue = totalSeconds,
            inputUnit = ValueUnit.SECONDS,
            time = time,
            hours = hours,
            minutes = minutes,
            seconds = seconds,
            normalizedTotalSeconds = normalizedTotalSeconds,
            wrappedToDay = daysOffset > 0,
            daysOffset = daysOffset
        )
    }
}
