package com.derra.taskyapp.data.mappers

import com.derra.taskyapp.data.objectsviewmodel.Attendee
import com.derra.taskyapp.data.objectsviewmodel.Event
import com.derra.taskyapp.data.objectsviewmodel.Photo
import com.derra.taskyapp.data.remote.dto.AttendeeDtoResponse
import com.derra.taskyapp.data.room.entity.EventEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun EventEntity.toEvent(): Event {
    val attendeesListType = object : TypeToken<List<AttendeeDtoResponse>>() {}.type
    val photosListType = object : TypeToken<List<Photo>>() {}.type

    val attendeesList: List<AttendeeDtoResponse> = Gson().fromJson(attendeesJson, attendeesListType)
    val photosList: List<Photo> = Gson().fromJson(photosJson, photosListType)

    return Event(
        id = id,
        title = title,
        description = description ?: "",
        from = startTime,
        to = to,
        remindAt = remindAt,
        host = host,
        isUserEventCreator = isUserEventCreator,
        attendees = attendeesList.map { it.toAttendee() },
        photos = photosList
    )
}

fun Event.toEventEntity(): EventEntity {
    val attendeesJson = Gson().toJson(attendees.map { it.toAttendeeDtoResponse() })
    val photosJson = Gson().toJson(photos)

    return EventEntity(
        id = id,
        title = title,
        description = description,
        startTime = from,
        to = to,
        remindAt = remindAt,
        host = host,
        isUserEventCreator = isUserEventCreator,
        attendeesJson = attendeesJson,
        photosJson = photosJson
    )
}