package com.derra.taskyapp.data.remote.dto

data class AttendeeResponseDto(
    val doesUserExist: Boolean,
    val attendee: AttendeeDto?
)
