package com.derra.taskyapp.presentation.task


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
import com.derra.taskyapp.data.mappers.toTaskEntity
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.objectsviewmodel.Task
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
class TaskViewModel @Inject constructor(
    private val repository: TaskyRepository,
    private val userManager: UserManager,
    private val context: Context,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var task by mutableStateOf<Task?>(null)
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
    var checked by mutableStateOf(false)
        private set
    var editMode by mutableStateOf(false)
        private set
    var token: String = ""


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
    var newTask by mutableStateOf(true)
        private set
    var minutesBefore by mutableStateOf(30)


    private val _uiEvent = Channel<UiEvent>()

    val uiEvent = _uiEvent.receiveAsFlow()



    init {
        val taskId = savedStateHandle.get<String>("taskId")!!
        val isEditable = savedStateHandle.get<Boolean>("isEditable")!!
        token = getLoginResponse()!!.token
        token = "Bearer $token"


        Log.d("TEST", "This is isEditable: $isEditable")

        editMode = isEditable

        if (taskId != "NONE") {
            newTask = false
            viewModelScope.launch {
                repository.getTaskItem(token, taskId).collectLatest {resource ->
                    when (resource) {
                        is Resource.Success -> {
                            task = resource.data
                            title = task?.title ?: ""
                            description = task?.description ?: ""
                            dateTime = task?.time!!
                            time = dateTime.toLocalTime()
                            minutesBefore = calculateReminderTime(dateTime, remindAt)
                            date = dateTime.toLocalDate()
                            remindAt = task?.remindAt!!
                            checked = task?.isDone ?: false


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

    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.EditTitleClick -> {
                editTitleMode = true
                tempString = title

            }
            is TaskEvent.OnTextChange -> {
                tempString = event.text
            }
            is TaskEvent.DifferentReminderTimeClick -> {
                reminderDropDown = false
                remindAt = dateTime.minusMinutes(event.minutes.toLong())
                minutesBefore = event.minutes
            }
            is TaskEvent.ReminderTimeDismiss -> {
                reminderDropDown = false

            }
            is TaskEvent.DeleteTaskClick -> {
                deleteDialog = true


            }
            is TaskEvent.EditDateClick -> {
                dateDialog = true

            }
            is TaskEvent.AdjustNotificationClick -> {
                reminderDropDown = true


            }
            is TaskEvent.EditDescriptionClick -> {
                editDescriptionMode = true
                tempString = description
            }
            is TaskEvent.EditTimeClick -> {
                timeDialog = true

            }

            is TaskEvent.OnBackButtonTextFieldClick -> {
                tempString = ""
                editDescriptionMode = false
                editTitleMode = false

            }
            is TaskEvent.OnCrossButtonClick -> {
                sendUiEvent(UiEvent.PopBackStack)

            }
            is TaskEvent.OnCheckedClick -> {
                checked = !checked
            }
            is TaskEvent.SaveNewDescriptionClick -> {
                description = tempString
                tempString = ""
                editDescriptionMode= false
            }
            is TaskEvent.SaveNewTitleClick -> {
                title = tempString
                tempString = ""
                editTitleMode = false

            }
            is TaskEvent.SaveButtonClick -> {

                if (newTask) {
                    viewModelScope.launch {
                        val id = UUID.randomUUID().toString()
                        repository.createTask(token, Task(id = id,
                            title = title, description = description, time = dateTime, remindAt = remindAt,
                            isDone=  checked).toTaskEntity())
                        val notification = NotificationEntity(id = id, name = title, description = description, itemType = 2, remindAt)
                        notification.let(scheduler::schedule)
                        repository.insertNotification(notification)
                    }
                }
                else {
                    viewModelScope.launch {
                        repository.updateTask(token,  Task(id = task!!.id, title = title,
                            description = description, time = dateTime, remindAt = remindAt, isDone = checked).toTaskEntity())
                        val notification = NotificationEntity(id = task!!.id, name = title, description = description,
                            itemType = 2, remindAt)
                        notification.let(scheduler::schedule)
                        repository.insertNotification(notification)
                    }
                }

                editMode = false

            }
            is TaskEvent.EditIconClick -> {
                editMode = true
            }

            is TaskEvent.OnDateChange -> {

                date = event.date
                dateTime = LocalDateTime.of(date, time)
                dateDialog = false
            }
            is TaskEvent.OnDateDismiss -> {
                dateDialog = false
            }
            is TaskEvent.OnTimeDismiss -> {
                timeDialog = false
            }
            is TaskEvent.OnTimeChange -> {
                time =  event.time


            }

            is TaskEvent.OnTimeSave -> {
                dateTime = LocalDateTime.of(date, time)
                timeDialog = false
            }
            is TaskEvent.DeleteCancelClick -> {
                deleteDialog = false
            }
            is TaskEvent.DeleteConfirmClick -> {
                deleteDialog = false
                if (task != null) {
                    viewModelScope.launch {
                        repository.deleteTaskItem(token, taskId = task!!.id)
                        val notification = repository.getNotificationById(task!!.id)
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