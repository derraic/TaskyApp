package com.derra.taskyapp.data.objectsviewmodel

data class AgendaItems(
    val events: List<Event>,
    val reminders: List<Reminder>,
    val tasks: List<Task>
)
