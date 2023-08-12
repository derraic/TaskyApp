package com.derra.taskyapp.presentation.agenda

import java.time.LocalDate
import java.time.LocalDateTime

sealed class DayTaskEvent{
    object AddItemClick: DayTaskEvent()
    object AddEventClick: DayTaskEvent()
    object AddTaskClick: DayTaskEvent()
    object AddReminderClick: DayTaskEvent()
    data class TaskItemCheckBoxClick(val checked: Boolean): DayTaskEvent()
    data class AnotherDayClick(val dayClicked: LocalDate): DayTaskEvent()
    object UserProfileClick: DayTaskEvent()
    object OpenAgendaDialogClick: DayTaskEvent()
    data class EditItemClick(val item: Any): DayTaskEvent()
    data class OpenItemClick(val item: Any): DayTaskEvent()
    data class DeleteItemClick(val item: Any): DayTaskEvent()
    object LogoutClick: DayTaskEvent()
    object ConfirmDialogCancelClick: DayTaskEvent()
    object ConfirmDialogOkClick: DayTaskEvent()

}



