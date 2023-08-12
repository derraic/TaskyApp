package com.derra.taskyapp.data.objectsviewmodel

import java.time.LocalDateTime

data class Attendee(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    var isGoing: Boolean,
    val RemindAt: LocalDateTime
)
