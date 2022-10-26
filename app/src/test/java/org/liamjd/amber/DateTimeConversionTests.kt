package org.liamjd.amber

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class DateTimeConversionTests {

    @Test
    fun `calculate difference between two dates`() {
        val now = LocalDateTime.of(2022, 10, 26, 21, 15)
        val monthAgo = LocalDateTime.of(2022, 9, 26, 21, 15)

        val daysBetween = ChronoUnit.DAYS.between(now, monthAgo)

        assertEquals(30L, daysBetween)
    }
}