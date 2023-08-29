package com.derra.taskyapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.derra.taskyapp.data.room.entity.*

@Database(
    entities = [EventEntity::class, TaskEntity::class, ReminderEntity::class, DeleteEntity::class, NotificationEntity::class],
    version = 2,
    exportSchema = false

)

@TypeConverters(LocalDateTimeConverter::class, LocalDateConverter::class)
abstract class TaskyDatabase: RoomDatabase() {
    abstract val dao: TaskyDao

}