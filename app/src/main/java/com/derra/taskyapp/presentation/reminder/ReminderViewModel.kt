package com.derra.taskyapp.presentation.reminder

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derra.taskyapp.data.NotificationAlarmScheduler
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.mappers.toReminderEntity
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
import com.derra.taskyapp.data.room.entity.NotificationEntity
import com.derra.taskyapp.util.Resource
import com.derra.taskyapp.util.UiEvent
import com.derra.taskyapp.util.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: TaskyRepository,
    private val userManager: UserManager,
    private val context: Context,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var reminder by mutableStateOf<Reminder?>(null)
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var dateTime by mutableStateOf<LocalDateTime>(LocalDateTime.now())
        private set
    var date by mutableStateOf<LocalDate>(LocalDate.now())
        private set
    var time by mutableStateOf<LocalTime>(LocalTime.now())
        private set
    var remindAt by mutableStateOf<LocalDateTime>(LocalDateTime.now().minusMinutes(30))
    var editMode by mutableStateOf(false)
        private set
    var minutesBefore by mutableStateOf(30)
    private var token: String = ""

    var editTitleMode by mutableStateOf(false)
        private set
    var editDescriptionMode by mutableStateOf(false)
        private set
    var timeDialog by mutableStateOf(false)
        private set
    var dateDialog by mutableStateOf(false)
        private set
    var reminderDropDown by mutableStateOf(false)
        private set
    var tempString by mutableStateOf("")
        private set
    var deleteDialog by mutableStateOf(false)
        private set
    var newReminder by mutableStateOf(true)
        private set



    private val _uiEvent = Channel<UiEvent>()

    val uiEvent = _uiEvent.receiveAsFlow()



    init {
        val reminderId = savedStateHandle.get<String>("reminderId")!!
        val isEditable = savedStateHandle.get<Boolean>("isEditable")!!
        token = getLoginResponse()!!.token
        token = "Bearer $token"

        Log.d("TEST", "This is isEditable: $isEditable")
        editMode = isEditable




        if (reminderId != "NONE") {
            newReminder = false

            viewModelScope.launch {
                repository.getReminderItem(token,reminderId ).collectLatest {resource ->
                    when (resource) {
                        is Resource.Success -> {
                            reminder = resource.data
                            title = reminder?.title ?: ""
                            description = reminder?.description ?: ""
                            dateTime = reminder?.time!!
                            remindAt = reminder?.remindAt!!
                            minutesBefore = calculateReminderTime(dateTime, remindAt)
                            time = dateTime.toLocalTime()!!
                            date = dateTime.toLocalDate()


                        }
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {

                        }


                    }


                }




            }





        }


    }
    private val scheduler = NotificationAlarmScheduler(context)

    fun onEvent(event: ReminderEvent) {
        when (event) {
            is ReminderEvent.EditTitleClick -> {
                editTitleMode = true
                tempString = title

            }
            is ReminderEvent.OnTextChange -> {
                tempString = event.text
            }
            is ReminderEvent.DifferentReminderTimeClick -> {
                reminderDropDown = false
                remindAt = dateTime.minusMinutes(event.minutes.toLong())
                minutesBefore = event.minutes
            }
            is ReminderEvent.ReminderTimeDismiss -> {
                reminderDropDown = false




            }
            is ReminderEvent.DeleteReminderClick -> {
                deleteDialog = true


            }
            is ReminderEvent.EditDateClick -> {
                dateDialog = true

            }
            is ReminderEvent.AdjustNotificationClick -> {
                reminderDropDown = true


            }
            is ReminderEvent.EditDescriptionClick -> {
                editDescriptionMode = true
                tempString = description
            }
            is ReminderEvent.EditTimeClick -> {
                timeDialog = true

            }

            is ReminderEvent.OnBackButtonTextFieldClick -> {
                tempString = ""
                editDescriptionMode = false
                editTitleMode = false

            }
            is ReminderEvent.OnCrossButtonClick -> {
                sendUiEvent(UiEvent.PopBackStack)

            }
            is ReminderEvent.SaveNewDescriptionClick -> {
                description = tempString
                tempString = ""
                editDescriptionMode = false
            }
            is ReminderEvent.SaveNewTitleClick -> {
                title = tempString
                tempString = ""
                editTitleMode = false

            }
            is ReminderEvent.SaveButtonClick -> {
                if (newReminder) {
                    viewModelScope.launch {
                        val id = UUID.randomUUID().toString()
                        repository.createReminder(token, Reminder(id = id,
                            title = title, description = description, time = dateTime, remindAt = remindAt).toReminderEntity())
                        val notification = NotificationEntity(id = id, name = title, description = description, itemType = 1, remindAt)
                        notification.let(scheduler::schedule)
                        repository.insertNotification(notification)
                    }


                }
                else {
                    viewModelScope.launch {
                        repository.updateReminder(token, Reminder(id = reminder!!.id, title = title,
                            description = description, time = dateTime, remindAt = remindAt).toReminderEntity())
                        val notification = NotificationEntity(id = reminder!!.id, name = title, description = description,
                            itemType = 1, remindAt)
                        notification.let(scheduler::schedule)
                        repository.insertNotification(notification)
                    }
                }

                editMode = false

            }
            is ReminderEvent.EditIconClick -> {
                editMode = true
            }

            is ReminderEvent.OnDateChange -> {

                date = event.date
                dateTime = LocalDateTime.of(date, time)
                dateDialog = false
            }
            is ReminderEvent.OnDateDismiss -> {
                dateDialog = false
            }
            is ReminderEvent.OnTimeDismiss -> {
                timeDialog = false
            }
            is ReminderEvent.OnTimeChange -> {
                time =  event.time


            }
            is ReminderEvent.OnTimeSave -> {
                dateTime = LocalDateTime.of(date, time)
                timeDialog = false
            }
            is ReminderEvent.DeleteCancelClick -> {
                deleteDialog = false
            }
            is ReminderEvent.DeleteConfirmClick -> {
                deleteDialog = false
                if (reminder != null) {
                    viewModelScope.launch {
                        repository.deleteReminderItem(token, reminderId = reminder!!.id)
                        val notification = repository.getNotificationById(reminder!!.id)
                        notification?.let(scheduler::cancel)

                    }
                    sendUiEvent(UiEvent.PopBackStack)
                }
                else {
                    sendUiEvent(UiEvent.PopBackStack)
                }


            }
        }
    }

    fun giveReminderString(minutesBefore: Int): String {
        return when (minutesBefore) {
            10 -> "10 minutes before"
            30 -> "30 minutes before"
            60 -> "1 hour before"
            360 -> "6 hours before"
            1440 -> "day before"
            else -> "$minutesBefore minutes before"
        }
    }

    private fun calculateReminderTime(dateTime: LocalDateTime, remindAt: LocalDateTime): Int {
        val duration = Duration.between(dateTime, remindAt)

        val minutes = duration.toMinutes()

        return when {
            minutes <= 10 -> 10
            minutes <= 30 -> 30
            minutes <= 60 -> 60
            minutes <= 360 -> 360
            minutes <= 1440 -> 1440
            else -> 1440
        }
    }

    private fun getLoginResponse(): LoginResponseDto? {
        return userManager.getLoginResponse()
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }




}