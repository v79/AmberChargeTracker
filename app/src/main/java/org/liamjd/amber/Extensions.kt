package org.liamjd.amber

import androidx.annotation.StringRes
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
fun NavController.getConfigLong(@StringRes key:Int): Long {
   return this.context.getSharedPreferences(this.context.resources.getString(R.string.CONFIG),0).getLong(this.context.resources.getString(key),-1L)
}

fun LocalDateTime.toLocalString(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return formatter.format(this)
}