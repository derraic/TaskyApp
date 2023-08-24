package com.derra.taskyapp.presentation.event

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class EventEvent {
    object SaveButtonClick: EventEvent()
    object EditIconClick: EventEvent()
    object DeleteEventClick: EventEvent()
    object EditTitleClick: EventEvent()
    object EditDescriptionClick: EventEvent()
    object AdjustNotificationClick: EventEvent()
    object EditFromTimeClick: EventEvent()
    object EditToTimeClick: EventEvent()

    object OnBackButtonTextFieldClick: EventEvent()
    object OnCrossButtonClick: EventEvent()
    object SaveNewTitleClick: EventEvent()
    object SaveNewDescriptionClick: EventEvent()
    object ReminderTimeDismiss: EventEvent()

    object EditFromDateClick: EventEvent()
    object EditToDateClick: EventEvent()


    data class OnDateChange(val date: LocalDate): EventEvent()
    object OnDateDismiss: EventEvent()
    data class DifferentReminderTimeClick (val minutes: Int): EventEvent()
    data class OnTextChange(val text: String): EventEvent()

    data class OnTimeChange(val time: LocalTime): EventEvent()
    object OnTimeDismiss: EventEvent()
    object OnTimeSave: EventEvent()

    object DeleteConfirmClick: EventEvent()
    object DeleteCancelClick: EventEvent()



}
