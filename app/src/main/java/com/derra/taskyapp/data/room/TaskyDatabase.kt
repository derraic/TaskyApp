package com.derra.taskyapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.derra.taskyapp.data.room.entity.EventEntity
import com.derra.taskyapp.data.room.entity.ReminderEntity
import com.derra.taskyapp.data.room.entity.TaskEntity

@Database(
    entities = [EventEntity::class, TaskEntity::class, ReminderEntity::class],
    version = 1,
    exportSchema = false

)

@TypeConverters(LocalDateTimeConverter::class, LocalDateConverter::class)
abstract class TaskyDatabase: RoomDatabase() {
    abstract val dao: TaskyDao

}