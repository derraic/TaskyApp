package com.derra.taskyapp.data.mappers

import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.room.entity.ReminderEntity

fun Reminder.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt
    )
}

fun ReminderEntity.toReminder(): Reminder {
    return Reminder(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt
    )
}