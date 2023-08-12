package com.derra.taskyapp.data.mappers_dto_to_entity

import com.derra.taskyapp.data.remote.dto.ReminderDto
import com.derra.taskyapp.data.room.entity.ReminderEntity
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

// Mapper from ReminderDto to ReminderEntity
fun ReminderDto.toReminderEntity(): ReminderEntity {
    return ReminderEntity(
        id = id,
        title = title,
        description = description ?: "",
        time = time.toLocalDateTimeInDeviceTimeZone(),
        remindAt = remindAt.toLocalDateTimeInDeviceTimeZone(),
        needsSync = false
    )
}

// Mapper from ReminderEntity to ReminderDto
fun ReminderEntity.toReminderDto(): ReminderDto {
    return ReminderDto(
        id = id,
        title = title,
        description = description,
        time = time.toTimestampInDeviceTimeZone(),
        remindAt = remindAt.toTimestampInDeviceTimeZone()
    )
}
