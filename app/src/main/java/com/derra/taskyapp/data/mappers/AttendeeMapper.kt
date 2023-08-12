package com.derra.taskyapp.data.mappers

import com.derra.taskyapp.data.objectsviewmodel.AttendeeInfo
import com.derra.taskyapp.data.objectsviewmodel.AttendeeIsGoing
import com.derra.taskyapp.data.remote.dto.AttendeeResponseDto

fun AttendeeResponseDto.toAttendeeIsGoing(): AttendeeIsGoing {
    val attendeeInfo = AttendeeInfo(
        email = attendee.email,
        fullName = attendee.fullName,
        userId = attendee.userId
    )
    return AttendeeIsGoing(
        doesUserExist = doesUserExist,
        attendeeInfo = attendeeInfo
    )
}