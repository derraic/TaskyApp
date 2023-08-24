package com.derra.taskyapp.presentation.event

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.mappers.toReminderEntity
import com.derra.taskyapp.data.objectsviewmodel.Attendee
import com.derra.taskyapp.data.objectsviewmodel.Event
import com.derra.taskyapp.data.objectsviewmodel.Photo
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
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
class EventViewModel @Inject constructor(
    private val repository: TaskyRepository,
    private val userManager: UserManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var token: String = ""
    var eventItem by mutableStateOf<Event?>(null)
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var fromDateTime by mutableStateOf<LocalDateTime>(LocalDateTime.now())
        private set
    var fromDate by mutableStateOf<LocalDate>(LocalDate.now())
        private set
    var fromTime by mutableStateOf<LocalTime>(LocalTime.now())
        private set
    var toDateTime by mutableStateOf<LocalDateTime>(LocalDateTime.now())
        private set
    var toDate by mutableStateOf<LocalDate>(LocalDate.now())
        private set
    var toTime by mutableStateOf<LocalTime>(LocalTime.now())
        private set
    var fromClicked by mutableStateOf(false)

    var remindAt by mutableStateOf<LocalDateTime>(LocalDateTime.now().minusMinutes(30))
    var editMode by mutableStateOf(false)
        private set
    var minutesBefore by mutableStateOf(30)
        private set

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
    var newEvent by mutableStateOf(true)
        private set
    var isHost by mutableStateOf(true)
        private set
    var hostId by mutableStateOf("")
        private set
    var attendees by mutableStateOf<List<Attendee>>(emptyList())
        private set
    var visitorsButtonSelected by mutableStateOf(0)
    var photos by mutableStateOf<List<Photo>>(emptyList())

    var photosDeleted by mutableStateOf<List<String>>(emptyList())




    private val _uiEvent = Channel<UiEvent>()

    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val eventId = savedStateHandle.get<String>("eventId")!!
        val isEditable = savedStateHandle.get<Boolean>("isEditable")!!
        token = getLoginResponse()!!.token
        if (isEditable) {
            editMode = true
        }

        if (eventId != "NONE") {
            newEvent = false


            viewModelScope.launch {
                repository.getEventItem(token,eventId).collectLatest {resource ->
                    when (resource) {
                        is Resource.Success -> {
                            eventItem = resource.data
                            title = eventItem?.id ?: ""
                            description = eventItem?.description ?: ""
                            fromDateTime = eventItem?.from!!

                            remindAt = eventItem?.remindAt!!

                            minutesBefore = calculateReminderTime(fromDateTime, remindAt = remindAt)
                            fromTime = fromDateTime.toLocalTime()!!
                            fromDate = fromDateTime.toLocalDate()
                            toDateTime = eventItem!!.to
                            toDate = toDateTime.toLocalDate()
                            toTime = toDateTime.toLocalTime()
                            isHost = eventItem!!.isUserEventCreator
                            attendees = attendees
                            photos = photos



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

    fun onEvent(event: EventEvent) {
        when (event) {
            is EventEvent.EditTitleClick -> {
                editTitleMode = true
                tempString = title

            }
            is EventEvent.OnTextChange -> {
                tempString = event.text
            }
            is EventEvent.DifferentReminderTimeClick -> {
                reminderDropDown = false
                remindAt = fromDateTime.minusMinutes(event.minutes.toLong())
                minutesBefore = event.minutes
            }
            is EventEvent.ReminderTimeDismiss -> {
                reminderDropDown = false

            }
            is EventEvent.DeleteEventClick -> {
                deleteDialog = true


            }
            is EventEvent.EditFromDateClick -> {
                fromClicked = true
                dateDialog = true

            }
            is EventEvent.EditToDateClick -> {
                fromClicked = false
                dateDialog = true

            }
            is EventEvent.AdjustNotificationClick -> {
                reminderDropDown = true


            }
            is EventEvent.EditDescriptionClick -> {
                editDescriptionMode = true
                tempString = description
            }
            is EventEvent.EditFromTimeClick -> {
                editTitleMode = true
                fromClicked = true

            }
            is EventEvent.EditToTimeClick -> {
                editTitleMode = true
                fromClicked = false

            }

            is EventEvent.OnBackButtonTextFieldClick -> {
                tempString = ""
                editDescriptionMode = false
                editTitleMode = false

            }
            is EventEvent.OnCrossButtonClick -> {
                sendUiEvent(UiEvent.PopBackStack)

            }
            is EventEvent.SaveNewDescriptionClick -> {
                description = tempString
                tempString = ""
            }
            is EventEvent.SaveNewTitleClick -> {
                title = tempString
                tempString = ""

            }
            is EventEvent.SaveButtonClick -> {
                if (newEvent) {
                    viewModelScope.launch {
                        repository.createReminder(token, Reminder(id = UUID.randomUUID().toString(), title = title, description = description, time = fromDateTime, remindAt = remindAt).toReminderEntity())
                    }

                }
                else {
                    viewModelScope.launch {
                        repository.updateReminder(token, Reminder(id = eventItem!!.id, title = title, description = description, time = fromDateTime, remindAt = remindAt).toReminderEntity())
                    }
                }

                editMode = false

            }
            is EventEvent.EditIconClick -> {
                editMode = true
            }

            is EventEvent.OnDateChange -> {

                if (fromClicked) {
                    fromDate = event.date
                    fromDateTime = LocalDateTime.of(fromDate, fromTime)
                }
                else {
                    toDate = event.date
                    toDateTime = LocalDateTime.of(toDate, toTime)

                }
                dateDialog = false
            }
            is EventEvent.OnDateDismiss -> {
                dateDialog = false
            }
            is EventEvent.OnTimeDismiss -> {
                timeDialog = false
            }
            is EventEvent.OnTimeChange -> {
                if (fromClicked)  {
                    fromTime =  event.time
                }
                else {
                    toTime = event.time
                }



            }
            is EventEvent.OnTimeSave -> {
                if (fromClicked) {
                    fromDateTime = LocalDateTime.of(fromDate, fromTime)
                }
                else {
                    toDateTime = LocalDateTime.of(toDate, toTime)
                }

                timeDialog = false
            }
            is EventEvent.DeleteCancelClick -> {
                deleteDialog = false
            }
            is EventEvent.DeleteConfirmClick -> {
                deleteDialog = false
                if (eventItem != null) {
                    viewModelScope.launch {
                        repository.deleteReminderItem(token, reminderId = eventItem!!.id)
                    }
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


    private fun isTwoWords(input: String): Boolean {
        val words = input.trim().split("\\s+".toRegex())
        return words.size == 2
    }

    private fun isOneWord(input: String): Boolean {
        val words = input.trim().split("\\s+".toRegex())
        return words.size == 1
    }

    fun getNameInitials(name: String) : String{
        return if (isOneWord(name)) {
            name.take(2)
        } else if (isTwoWords(name)) {
            val words = name.trim().split("\\s+".toRegex())
            "${words[0].first()}${words[1].first()}"
        } else {
            val words = name.trim().split("\\s+".toRegex())
            "${words[0].first()}${words.last().first()}"
        }


    }




}