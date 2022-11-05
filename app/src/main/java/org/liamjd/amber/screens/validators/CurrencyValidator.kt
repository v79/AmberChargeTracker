package org.liamjd.amber.screens.validators

import android.icu.text.DecimalFormat
import android.util.Log
import java.text.ParseException

interface FieldValidator {
    fun validate(input: String): ValidationResult
}

sealed class ValidationResult(var isValid: Boolean = true) {
    object Valid : ValidationResult()
    class Invalid(val message: String) : ValidationResult(isValid = false)
    class Warning(val message: String) : ValidationResult()
}

object CurrencyValidator : FieldValidator {
    private val format = DecimalFormat.getInstance()
    override fun validate(input: String): ValidationResult {
        format.maximumFractionDigits = 2
        try {
            val currency = format.parse(input)
        } catch (pe: ParseException) {
            Log.e("CurrencyValidator", "Could not parse input: $input")
            return ValidationResult.Invalid("Invalid currency")
        }
        return ValidationResult.Valid
    }
}

object PercentageValidator : FieldValidator {
    private val format = DecimalFormat.getInstance()
    override fun validate(input: String): ValidationResult {
        format.maximumFractionDigits = 2
        try {
            val percentage = format.parse(input)
            if (percentage.toInt() > 100) {
                return ValidationResult.Invalid("Percentage must be < 100")
            }
            if (percentage.toInt() < 0) {
                return ValidationResult.Warning("Are you sure?")
            }
        } catch (pe: ParseException) {
            Log.e("CurrencyValidator", "Could not parse input: $input")
            return ValidationResult.Invalid("Invalid")
        }
        return ValidationResult.Valid
    }
}