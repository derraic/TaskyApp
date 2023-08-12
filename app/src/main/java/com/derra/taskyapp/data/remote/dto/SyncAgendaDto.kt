package com.derra.taskyapp.data.remote.dto

data class SyncAgendaDto(
    val deletedEventIds: List<String>,
    val deletedTaskIds: List<String>,
    val deletedReminderIds: List<String>
)
