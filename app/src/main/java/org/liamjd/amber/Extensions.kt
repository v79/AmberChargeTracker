package org.liamjd.amber

import android.icu.number.NumberFormatter
import android.icu.text.NumberFormat
import androidx.annotation.StringRes
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.NavController
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
    val numberFormat = NumberFormat.getCurrencyInstance()
    return numberFormat.format((this / 100).toLong())
}

/**
 * Parse a currency string and convert it to an integer value, or null on failure
 */
fun String.currencyToIntOrNull(): Int? {
    val numberFormat = NumberFormat.getCurrencyInstance()
    try {
        val value = numberFormat.parse(this)
        return (value.toLong() * 100).toInt()
    } catch (e: Exception) {
        return null
    }
}