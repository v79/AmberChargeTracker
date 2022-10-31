package org.liamjd.amber.screens.validators

import android.icu.text.DecimalFormat
import android.util.Log
import java.text.ParseException

interface FieldValidator {
    fun validate(input: String): Boolean
}

object CurrencyValidator : FieldValidator {
    private val format = DecimalFormat.getInstance()
    override fun validate(input: String): Boolean {
        format.maximumFractionDigits = 2
        try {
            val currency = format.parse(input)
        } catch (pe: ParseException) {
            Log.e("CurrencyValidator", "Could not parse input: $input")
            return false
        }
        return true
    }
}