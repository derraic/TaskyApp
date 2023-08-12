package com.derra.taskyapp.data.objectsviewmodel


import java.time.LocalDateTime

data class Event(
    val id: String,
    val title: String,
    val description: String,
    val from: LocalDateTime,
    val to: LocalDateTime,
    val remindAt: LocalDateTime,
    val host: String,
    val isUserEventCreator: Boolean,
    val attendees: List<Attendee>,
    var photos: List<Photo>

)
