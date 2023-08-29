package com.derra.taskyapp.presentation.event

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import com.derra.taskyapp.data.objectsviewmodel.Attendee
import com.derra.taskyapp.data.objectsviewmodel.Photo
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

    data class OnEmailAddressChange(val emailAddress: String): EventEvent()

    object AddAttendeeModalOpen: EventEvent()
    object AddAttendeeModalDissmiss: EventEvent()

    object AddAttendeeModalButtonCLick: EventEvent()

    object LeaveOrJoinEventClick: EventEvent()
    data class GoingButtonClick(val number: Int): EventEvent()

    data class DeleteAttendeeClick(val attendee: Attendee): EventEvent()

    data class AddPhotoClick(val galleryLauncher: ActivityResultLauncher<String>): EventEvent()

    object PhotoScreenCloseClick: EventEvent()
    object PhotoScreenDeleteClick: EventEvent()

    data class OnImageChange(val uri: Uri): EventEvent()

    data class PhotoScreenOpenClick(val photo: Photo): EventEvent()

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
