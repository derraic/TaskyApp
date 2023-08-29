package com.derra.taskyapp.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.derra.taskyapp.data.room.LocalDateTimeConverter
import java.time.LocalDateTime

@Entity("notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val itemType: Int,
    @TypeConverters(LocalDateTimeConverter::class)
    val remindAt: LocalDateTime,


    // 0 = event, 1 = reminder, 2 = task

)
