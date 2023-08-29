package com.derra.taskyapp.data.mappers

import com.derra.taskyapp.data.mappers_dto_to_entity.getDeviceTimeZone
import com.derra.taskyapp.data.mappers_dto_to_entity.toLocalDateTimeInDeviceTimeZone
import com.derra.taskyapp.data.mappers_dto_to_entity.toTimestampInDeviceTimeZone
import com.derra.taskyapp.data.objectsviewmodel.Attendee
import com.derra.taskyapp.data.objectsviewmodel.AttendeeInfo
import com.derra.taskyapp.data.objectsviewmodel.AttendeeIsGoing
import com.derra.taskyapp.data.remote.dto.AttendeeDtoResponse
import com.derra.taskyapp.data.remote.dto.AttendeeResponseDto
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun AttendeeResponseDto.toAttendeeIsGoing(): AttendeeIsGoing {
    if (attendee == null) {
        return AttendeeIsGoing(
            doesUserExist = doesUserExist,
            attendeeInfo = null
        )


    }
    val attendeeInfo = AttendeeInfo(
        email = attendee?.email ?: "",
        fullName = attendee?.fullName ?: "",
        userId = attendee?.userId ?: ""
    )
    return AttendeeIsGoing(
        doesUserExist = doesUserExist,
        attendeeInfo = attendeeInfo
    )
}

fun AttendeeDtoResponse.toAttendee(): Attendee {
    return Attendee(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toLocalDateTimeInDeviceTimeZone()
    )
}

fun Attendee.toAttendeeDtoResponse() : AttendeeDtoResponse {
    return AttendeeDtoResponse(
        email = email,
        fullName = fullName,
        userId = userId,
        eventId = eventId,
        isGoing = isGoing,
        remindAt = remindAt.toTimestampInDeviceTimeZone()
    )

}




