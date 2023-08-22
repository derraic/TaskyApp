package com.derra.taskyapp.presentation.reminder

import com.derra.taskyapp.data.objectsviewmodel.Reminder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class ReminderEvent {
    object SaveOrEditButtonClick: ReminderEvent()
    object DeleteTaskClick: ReminderEvent()
    object EditTitleClick: ReminderEvent()
    object EditDescriptionClick: ReminderEvent()
    object AdjustNotificationClick: ReminderEvent()
    object EditTimeClick: ReminderEvent()
    object EditDateClick: ReminderEvent()
    object OnBackButtonTextFieldClick: ReminderEvent()
    object OnCrossButtonClick: ReminderEvent()
    data class SaveNewTitleClick(val title: String): ReminderEvent()
    data class SaveNewDescriptionClick(val description: String): ReminderEvent()
    data class ReminderTimeClick(val time: LocalDateTime): ReminderEvent()
    data class NewDateClick(val date: LocalDate): ReminderEvent()
    data class TimeChange(val time: LocalTime): ReminderEvent()



}
