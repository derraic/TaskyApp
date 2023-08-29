package com.derra.taskyapp.data.mappers_dto_to_entity

import com.derra.taskyapp.data.mappers.toEvent
import com.derra.taskyapp.data.mappers.toEventEntity
import com.derra.taskyapp.data.objectsviewmodel.Attendee
import com.derra.taskyapp.data.objectsviewmodel.Photo
import com.derra.taskyapp.data.remote.dto.AttendeeDtoResponse
import com.derra.taskyapp.data.remote.dto.EventDto
import com.derra.taskyapp.data.remote.dto.EventResponseDto
import com.derra.taskyapp.data.remote.dto.EventUpdateDto
import com.derra.taskyapp.data.room.TaskyDao
import com.derra.taskyapp.data.room.entity.EventEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

// Gson instance for serialization/deserialization
private val gson = Gson()

// Mapper from EventResponseDto to EventEntity
fun EventResponseDto.toEventEntity(): EventEntity {
    return EventEntity(
        id = id,
        title = title,
        description = description ?: "",
        startTime = from.toLocalDateTimeInDeviceTimeZone(),
        to = to.toLocalDateTimeInDeviceTimeZone(),
        remindAt = remindAt.toLocalDateTimeInDeviceTimeZone(),
        host = host,
        isUserEventCreator = isUserEventCreator,
        attendeesJson = gson.toJson(attendees), // Convert List<Attendee> to JSON string
        photosJson = gson.toJson(photos), // Convert List<Photo> to JSON string
        needsSync = false,

    )
}

fun EventEntity.toEventUpdateDto(dao: TaskyDao): EventUpdateDto{
    /*var isGoing: Boolean = false
    val event = this.toEvent()
    for (i in event.attendees) {
        if ( i.userId == userId) {
            isGoing = i.isGoing
        }
    }
    */
    return EventUpdateDto(
        id = id,
        title = title,
        description = description ?: "",
        from = startTime.toTimestampInDeviceTimeZone(),
        to = to.toTimestampInDeviceTimeZone(),
        remindAt = remindAt.toTimestampInDeviceTimeZone(),
        attendeeIds = parseAttendeesJson(attendeesJson).map { it.userId },
        isGoing = isGoing,
        deletedPhotoKeys = emptyList()


    )

}

fun EventDto.toEventEntity(host: String): EventEntity {
    return  EventEntity(
        id = id,
        title = title,
        description = description,
        startTime = from.toLocalDateTimeInDeviceTimeZone(),
        to = to.toLocalDateTimeInDeviceTimeZone(),
        remindAt = remindAt.toLocalDateTimeInDeviceTimeZone(),
        host = host,
        isUserEventCreator = true,
        attendeesJson = "",
        photosJson = "",
        needsSync = true,
        kindOfSync = "POST"

    )
}

suspend fun EventUpdateDto.toEventEntity(dao: TaskyDao, userId: String): EventEntity? {
    val event = dao.getEventById(id)?.toEvent()
    if (event != null) {
        for (i in event.attendees) {
            if (i.userId == userId){
                i.isGoing = isGoing
            }
        }
    }
    if (event != null) {
        return event.toEventEntity().copy(id = id, title = title, description = description, startTime = from.toLocalDateTimeInDeviceTimeZone(), to = to.toLocalDateTimeInDeviceTimeZone(),remindAt = remindAt.toLocalDateTimeInDeviceTimeZone(), needsSync = true, kindOfSync = "UPDATE")
    }
    else  {
        return null
    }



}

// Mapper from EventEntity to EventResponseDto

fun EventEntity.toEventDto(): EventDto {
    return EventDto(
        id = id,
        title = title,
        description = description ?: "",
        from = startTime.toTimestampInDeviceTimeZone(),
        to = to.toTimestampInDeviceTimeZone(),
        remindAt = remindAt.toTimestampInDeviceTimeZone(),
        attendeeIds = parseAttendeesJson(attendeesJson).map { it.userId },
    )
}
fun EventEntity.toEventResponseDto(): EventResponseDto {
    return EventResponseDto(
        id = id,
        title = title,
        description = description,
        from = startTime.toTimestampInDeviceTimeZone(),
        to = to.toTimestampInDeviceTimeZone(),
        remindAt = remindAt.toTimestampInDeviceTimeZone(),
        host = host,
        isUserEventCreator = isUserEventCreator,
        attendees = parseAttendeesJson(attendeesJson), // Convert JSON string to List<Attendee>
        photos = parsePhotosJson(photosJson) // Convert JSON string to List<Photo>
    )
}

// Helper function to convert JSON string to List<Attendee>
private fun parseAttendeesJson(json: String): List<AttendeeDtoResponse> {
    val listType: Type = object : TypeToken<List<AttendeeDtoResponse>>() {}.type
    return gson.fromJson(json, listType)
}

// Helper function to convert JSON string to List<Photo>
private fun parsePhotosJson(json: String): List<Photo> {
    val listType: Type = object : TypeToken<List<Photo>>() {}.type
    return gson.fromJson(json, listType)
}

fun LocalDateTime.toTimestampInDeviceTimeZone(): Long {
    val deviceTimeZone: ZoneId = getDeviceTimeZone()
    return this.atZone(deviceTimeZone).toInstant().toEpochMilli()
}

fun getDeviceTimeZone(): ZoneId {
    return ZoneId.of(TimeZone.getDefault().id)
}

// Function to convert Unix timestamp to LocalDateTime in the device's timezone
fun Long.toLocalDateTimeInDeviceTimeZone(): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(getDeviceTimeZone()).toLocalDateTime()
}