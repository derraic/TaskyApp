package com.derra.taskyapp.presentation.agenda

import com.derra.taskyapp.data.objectsviewmodel.Task
import java.time.LocalDate

sealed class DayTaskEvent{
    object AddItemClick: DayTaskEvent()
    object AddEventClick: DayTaskEvent()

    object DissmissDatePickerDialog: DayTaskEvent()

    data class DifferentDaySelected(val localDate: LocalDate): DayTaskEvent()
    object AddTaskClick: DayTaskEvent()
    object AddReminderClick: DayTaskEvent()
    data class TaskItemCheckBoxClick(val checked: Boolean, val task: Task): DayTaskEvent()
    data class AnotherDayClick(val dayClicked: LocalDate): DayTaskEvent()
    object UserProfileClick: DayTaskEvent()
    object OpenAgendaDialogClick: DayTaskEvent()
    data class IconEditItemClick(val item: Any): DayTaskEvent()

    object EditItemClick: DayTaskEvent()
    object OpenItemClick: DayTaskEvent()
    object DeleteItemClick: DayTaskEvent()
    object LogoutClick: DayTaskEvent()
    object ConfirmDialogCancelClick: DayTaskEvent()
    object ConfirmDialogOkClick: DayTaskEvent()
    object EditItemsDialogDismiss: DayTaskEvent()

    object LogOutDialogDismiss: DayTaskEvent()

    object AddItemDialogDismiss: DayTaskEvent()


}



