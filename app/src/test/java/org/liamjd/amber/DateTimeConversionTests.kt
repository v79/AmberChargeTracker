package org.liamjd.amber

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

class DateTimeConversionTests {

    @Test
    fun `calculate difference between two dates`() {
        val now = LocalDateTime.of(2022, 10, 26, 21, 15)
        val monthAgo = LocalDateTime.of(2022, 9, 26, 21, 15)

        val daysBetween = ChronoUnit.DAYS.between(now, monthAgo).absoluteValue

        assertEquals(30L, daysBetween)
    }

    @Test
    fun `get dates more than 7 days ago`() {
        val days = listOf(
            LocalDateTime.now(),
            LocalDateTime.of(2022, 10, 20, 15, 23),
            LocalDateTime.of(2021, 7, 1, 7, 15)
        )
        val now = LocalDateTime.now()
        println(days.size)
        days.forEach {
            println(ChronoUnit.DAYS.between(now, it))
        }
        val filteredList = days.filter { ChronoUnit.DAYS.between(now, it).absoluteValue > 7 }

        assertEquals(2, filteredList.size)
    }
}