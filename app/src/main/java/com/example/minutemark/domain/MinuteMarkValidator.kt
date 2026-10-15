package com.example.minutemark.domain

import com.example.minutemark.data.model.ValueUnit

sealed class ValidationResult<out T> {
    data class Valid<out T>(val value: T) : ValidationResult<T>()
    data class Invalid(val message: String) : ValidationResult<Nothing>()
}

object MinuteMarkValidator {

    const val MAX_SAFE_SECONDS = 1_000_000_000L
    const val MAX_SAFE_MINUTES = 1_000_000_000L

    /**
     * Validates seconds input field (0..59).
     */
    fun validateSeconds(input: String?): ValidationResult<Int> {
        val trimmed = input?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            return ValidationResult.Invalid("Enter a whole number between 0 and 59.")
        }
        if (trimmed.contains('.') || trimmed.contains(',')) {
            return ValidationResult.Invalid("Enter a non-negative whole number.")
        }
        if (trimmed.startsWith('-')) {
            return ValidationResult.Invalid("Enter a non-negative whole number.")
        }
        val parsed = trimmed.toIntOrNull()
            ?: return ValidationResult.Invalid("Enter a whole number between 0 and 59.")

        if (parsed !in 0..59) {
            return ValidationResult.Invalid("Seconds must be between 0 and 59.")
        }
        return ValidationResult.Valid(parsed)
    }

    /**
     * Validates numerical input for reverse conversion.
     * Enforces whole non-negative numbers and overflow protection.
     */
    fun validateReverseValue(input: String?, unit: ValueUnit): ValidationResult<Long> {
        val trimmed = input?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            return ValidationResult.Invalid("Enter a non-negative whole number.")
        }
        if (trimmed.contains('.') || trimmed.contains(',')) {
            return ValidationResult.Invalid("Enter a non-negative whole number.")
        }
        if (trimmed.startsWith('-')) {
            return ValidationResult.Invalid("Enter a non-negative whole number.")
        }
        // Check for any non-digit character
        if (!trimmed.all { it.isDigit() }) {
            return ValidationResult.Invalid("Enter a non-negative whole number.")
        }

        val parsed = try {
            trimmed.toLong()
        } catch (e: NumberFormatException) {
            return ValidationResult.Invalid("Value is too large.")
        }

        if (parsed < 0L) {
            return ValidationResult.Invalid("Enter a non-negative whole number.")
        }

        val maxAllowed = when (unit) {
            ValueUnit.SECONDS -> MAX_SAFE_SECONDS
            ValueUnit.MINUTES -> MAX_SAFE_MINUTES
        }

        if (parsed > maxAllowed) {
            return ValidationResult.Invalid("Value is too large.")
        }

        return ValidationResult.Valid(parsed)
    }
}
