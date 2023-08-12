package com.derra.taskyapp.data.remote.dto

data class AgendaDto(
    val events: List<EventResponseDto>,
    val tasks: List<TaskDto>,
    val reminders: List<ReminderDto>
)
