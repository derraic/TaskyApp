package com.derra.taskyapp.data.remote.dto

import java.time.LocalDateTime

data class AttendeeDtoResponse(
    val email: String,
    val fullName: String,
    val userId: String,
    val eventId: String,
    var isGoing: Boolean,
    val remindAt: Long,

    )
