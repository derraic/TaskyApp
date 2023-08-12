package com.derra.taskyapp.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.derra.taskyapp.data.room.LocalDateTimeConverter
import java.time.LocalDateTime

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    @TypeConverters(LocalDateTimeConverter::class)
    val startTime: LocalDateTime,
    @TypeConverters(LocalDateTimeConverter::class)
    val to: LocalDateTime,
    @TypeConverters(LocalDateTimeConverter::class)
    val remindAt: LocalDateTime,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendeesJson: String,
    val photosJson: String,
    var needsSync: Boolean = true,
    val kindOfSync: String = "none"

)
