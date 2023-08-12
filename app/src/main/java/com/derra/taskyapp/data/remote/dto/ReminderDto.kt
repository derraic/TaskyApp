package com.derra.taskyapp.data.remote.dto

data class ReminderDto(
    val id: String,
    val title: String,
    val description: String?,
    val time: Long,
    val remindAt: Long

)
