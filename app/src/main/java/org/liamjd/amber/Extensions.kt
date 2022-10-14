package org.liamjd.amber

/**
 * Convert the given String to an integer, and if it fails, set it to zero
 */
fun String.toIntOrZero(): Int {
    return this.toIntOrNull() ?: 0
}