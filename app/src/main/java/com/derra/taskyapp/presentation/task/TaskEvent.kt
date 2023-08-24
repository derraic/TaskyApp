package com.derra.taskyapp.presentation.task

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

sealed class TaskEvent{
    object SaveButtonClick: TaskEvent()
    object EditIconClick: TaskEvent()
    object DeleteTaskClick: TaskEvent()
    object EditTitleClick: TaskEvent()
    object EditDescriptionClick: TaskEvent()
    object AdjustNotificationClick: TaskEvent()
    object EditTimeClick: TaskEvent()
    object EditDateClick: TaskEvent()
    object OnBackButtonTextFieldClick: TaskEvent()
    object OnCrossButtonClick: TaskEvent()
    object SaveNewTitleClick: TaskEvent()
    object SaveNewDescriptionClick: TaskEvent()
    object ReminderTimeDismiss: TaskEvent()

    data class OnTimeChange(val time: LocalTime): TaskEvent()
    data class OnDateChange(val date: LocalDate): TaskEvent()
    object OnDateDismiss: TaskEvent()
    data class DifferentReminderTimeClick (val minutes: Int):TaskEvent()
    data class OnTextChange(val text: String): TaskEvent()
    object OnTimeDismiss: TaskEvent()
    object OnTimeSave: TaskEvent()
    object OnCheckedClick: TaskEvent()

    object DeleteConfirmClick: TaskEvent()
    object DeleteCancelClick: TaskEvent()
}
