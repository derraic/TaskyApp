package com.derra.taskyapp.data.remote.dto

data class EventUpdateDto(
    val id: String,
    val title: String,
    val description: String,
    val from: Long,
    val to: Long,
    val remindAt: Long,
    val attendeeIds: List<String>,
    val isGoing: Boolean,
    val deletedPhotoKeys: List<String>
)
