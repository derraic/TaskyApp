package com.derra.taskyapp.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.derra.taskyapp.data.room.LocalDateTimeConverter
import java.time.LocalDateTime

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String? = "",
    @TypeConverters(LocalDateTimeConverter::class)
    val time: LocalDateTime,
    @TypeConverters(LocalDateTimeConverter::class)
    val remindAt: LocalDateTime,
    val isDone: Boolean,
    var needsSync: Boolean = true,
    val kindOfSync: String = "none"
)
