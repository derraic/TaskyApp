package com.derra.taskyapp.data.room

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class LocalDateConverter {
    @TypeConverter
    fun fromLocalDate(localDate: LocalDate): Long {
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

    }

    @TypeConverter
    fun toLocalDate(epochSecond: Long): LocalDate {
        return Instant.ofEpochMilli(epochSecond).atZone(ZoneId.systemDefault()).toLocalDate()
    }
}