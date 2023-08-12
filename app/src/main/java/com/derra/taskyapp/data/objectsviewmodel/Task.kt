package com.derra.taskyapp.data.objectsviewmodel

import java.time.LocalDateTime

data class Task(
    val id: String,
    val title: String,
    val description: String? = null,
    val time: LocalDateTime,
    val remindAt: LocalDateTime,
    val isDone: Boolean
)
