package org.liamjd.amber.db

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

class DBConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC); }
    }

    @TypeConverter
    fun dateToTimestamp(timeStamp: LocalDateTime?): Long? {
        return timeStamp?.toEpochSecond(ZoneOffset.UTC)
    }

}