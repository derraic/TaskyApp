package com.derra.taskyapp.presentation.reminder

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class ReminderEvent {
    object SaveButtonClick: ReminderEvent()
    object EditIconClick: ReminderEvent()
    object DeleteReminderClick: ReminderEvent()
    object EditTitleClick: ReminderEvent()
    object EditDescriptionClick: ReminderEvent()
    object AdjustNotificationClick: ReminderEvent()
    object EditTimeClick: ReminderEvent()
    object EditDateClick: ReminderEvent()
    object OnBackButtonTextFieldClick: ReminderEvent()
    object OnCrossButtonClick: ReminderEvent()
    object SaveNewTitleClick: ReminderEvent()
    object SaveNewDescriptionClick: ReminderEvent()
    object ReminderTimeDismiss: ReminderEvent()
    data class OnTimeChange(val time: LocalTime): ReminderEvent()
    data class OnDateChange(val date: LocalDate): ReminderEvent()
    object OnDateDismiss: ReminderEvent()
    data class DifferentReminderTimeClick (val minutes: Int): ReminderEvent()
    data class OnTextChange(val text: String): ReminderEvent()
    object OnTimeDismiss: ReminderEvent()
    object OnTimeSave: ReminderEvent()

    object DeleteConfirmClick: ReminderEvent()
    object DeleteCancelClick: ReminderEvent()



}
