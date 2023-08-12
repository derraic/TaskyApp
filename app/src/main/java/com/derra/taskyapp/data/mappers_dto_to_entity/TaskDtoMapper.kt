package com.derra.taskyapp.data.mappers_dto_to_entity

import com.derra.taskyapp.data.remote.dto.TaskDto
import com.derra.taskyapp.data.room.entity.TaskEntity

fun TaskDto.toTaskEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description ?: "",
        time = time.toLocalDateTimeInDeviceTimeZone(),
        remindAt = remindAt.toLocalDateTimeInDeviceTimeZone(),
        isDone = isDone,
        needsSync = false
    )
}

// Mapper from TaskEntity to TaskDto
fun TaskEntity.toTaskDto(): TaskDto {
    return TaskDto(
        id = id,
        title = title,
        description = description,
        time = time.toTimestampInDeviceTimeZone(),
        remindAt = remindAt.toTimestampInDeviceTimeZone(),
        isDone = isDone
    )
}