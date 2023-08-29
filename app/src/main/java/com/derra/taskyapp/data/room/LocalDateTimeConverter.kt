package com.derra.taskyapp.data.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class LocalDateTimeConverter {
    @TypeConverter
    fun fromLocalDateTime(localDateTime: LocalDateTime): Long {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    @TypeConverter
    fun toLocalDateTime(epochSecond: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochSecond), ZoneId.systemDefault())
    }
}