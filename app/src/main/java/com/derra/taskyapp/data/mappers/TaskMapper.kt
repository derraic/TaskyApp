package com.derra.taskyapp.data.mappers

import com.derra.taskyapp.data.objectsviewmodel.Task
import com.derra.taskyapp.data.room.entity.TaskEntity

fun Task.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
        isDone = isDone
    )
}

fun TaskEntity.toTask(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        time = time,
        remindAt = remindAt,
        isDone = isDone
    )
}