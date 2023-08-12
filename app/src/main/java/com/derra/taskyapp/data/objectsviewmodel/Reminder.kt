package com.derra.taskyapp.data.objectsviewmodel

import java.time.LocalDateTime

data class Reminder(
    val id: String,
    val title: String,
    val description: String? = null,
    val time: LocalDateTime,
    val remindAt: LocalDateTime,
)
