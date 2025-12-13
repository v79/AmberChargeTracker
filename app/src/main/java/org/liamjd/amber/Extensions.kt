package org.liamjd.amber

import android.icu.text.NumberFormat
import android.util.Log
import androidx.annotation.StringRes
import androidx.navigation.NavController
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Convert the given String to an integer, and if it fails, set it to zero
 */
fun String.toIntOrZero(): Int {
    return this.toIntOrNull() ?: 0
}

/**
 * Get a Long value from the shared preferences with the given key
 * @param key String resource key to find
 * @return long value of the key, or -1 if not found
 */
fun NavController.getConfigLong(@StringRes key: Int): Long {
    return this.context.getSharedPreferences(this.context.resources.getString(R.string.CONFIG), 0)
        .getLong(this.context.resources.getString(key), -1L)
}

fun LocalDateTime.toLocalString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return formatter.format(this)
}

/**
 * Format an integer currency value (stored in pence or the local equivalent)
 */
fun Int.toCurrencyString(): String {
    // convert to Double, divide by 100.0, and format
    val costAsDouble = (this.toDouble()) / 100.0
    val numberFormat = NumberFormat.getCurrencyInstance()
    return numberFormat.format(costAsDouble)
}

/**
 * Parse a currency string and convert it to an integer value, or null on failure
 * This uses NumberFormat (locale-aware) and falls back to a BigDecimal parse via parseToPenceOrNull
 */
fun String.currencyToIntOrNull(): Int? {
    val numberFormat = NumberFormat.getCurrencyInstance()
    try {
        val value: Double = numberFormat.parse(this).toDouble()
        return (value * 100L).toInt()
    } catch (e: Exception) {
        // fallback to BigDecimal parser for free-form input like "0.79" or "0,79"
        return this.parseToPenceOrNull()
    }
}

/**
 * Parse a decimal string to pence using BigDecimal for exactness.
 * Accepts inputs like "0.79", "£0.79", "0,79" (comma decimal), and returns pence as Int
 */
fun String.parseToPenceOrNull(): Int? {
    try {
        var cleaned = this.trim()
        // remove currency symbol and spaces
        cleaned = cleaned.replace(Regex("[^0-9,.-]"), "")
        if (cleaned.isBlank()) return null
        // normalize comma to dot
        cleaned = cleaned.replace(',', '.')
        val bd = BigDecimal(cleaned)
        val pence = bd.multiply(BigDecimal(100)).setScale(0, RoundingMode.HALF_UP)
        return try {
            pence.intValueExact()
        } catch (e: ArithmeticException) {
            // If it's too large for Int, log and return null
            Log.e("parseToPenceOrNull", "Value too large to fit in Int: $pence")
            null
        }
    } catch (e: Exception) {
        Log.e("parseToPenceOrNull", "Unable to parse '$this' to pence: ${e.message}")
        return null
    }
}

fun Float.format(digits: Int = 0) = "%.${digits}f".format(this)