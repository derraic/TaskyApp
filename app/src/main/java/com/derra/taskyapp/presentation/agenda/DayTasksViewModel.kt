package com.derra.taskyapp.presentation.agenda

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.derra.taskyapp.SyncWorker
import com.derra.taskyapp.data.NotificationAlarmScheduler
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.mappers.toTaskEntity
import com.derra.taskyapp.data.mappers_dto_to_entity.getDeviceTimeZone
import com.derra.taskyapp.data.objectsviewmodel.Event
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.objectsviewmodel.Task
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
import com.derra.taskyapp.util.Resource
import com.derra.taskyapp.util.Routes
import com.derra.taskyapp.util.UiEvent
import com.derra.taskyapp.util.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class DayTasksViewModel @Inject constructor(
    private val repository: TaskyRepository,
    private val context: Context,
    private val userManager: UserManager
): ViewModel(){


    private var userInfo: LoginResponseDto? = null
    var name by mutableStateOf("")
    var daySelected by mutableStateOf<LocalDate>(LocalDate.now())
    var token: String = ""
    var isLoading by mutableStateOf(false)
    var events by mutableStateOf<List<Event>>(emptyList())
    var tasks by mutableStateOf<List<Task>>(emptyList())
    var reminders by mutableStateOf<List<Reminder>>(emptyList())
    var sortedList by mutableStateOf<List<Any>>(emptyList())
    var deleteDialog by mutableStateOf(false)
    var logoutDropDownDialog by mutableStateOf(false)
    var editDropDownDialog by mutableStateOf(false)
    var addItemDropDownDialog by mutableStateOf(false)
    var agendaDialog by mutableStateOf(false)

    var currItemSelected: Any? = null

    private val _uiEvent = Channel<UiEvent>()

    val uiEvent = _uiEvent.receiveAsFlow()


    var permissionNotification by mutableStateOf(false)
    

    var mergedList by mutableStateOf<List<Any>>(reminders + tasks + events)
        private set
    init {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionNotification = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else{
            permissionNotification = true
        }

        userInfo = getLoginResponse()
        token = userInfo?.token ?: ""
        token = "Bearer $token"
        name = userInfo?.fullName ?: ""
        name = getNameInitials(name)
        daySelected = LocalDate.now()
        getDayThings(LocalDateTime.now())

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Require unmetered network (Wi-Fi)
            .build()

        val inputData = Data.Builder()
            .putString(SyncWorker.TOKEN_KEY, token)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(syncWorkRequest)




    }

    fun getDayOfWeek(localDate: LocalDate): String {
        return when (localDate.dayOfWeek) {
            DayOfWeek.MONDAY -> "Monday"
            DayOfWeek.TUESDAY -> "Tuesday"
            DayOfWeek.WEDNESDAY -> "Wednesday"
            DayOfWeek.THURSDAY -> "Thursday"
            DayOfWeek.FRIDAY -> "Friday"
            DayOfWeek.SATURDAY -> "Saturday"
            DayOfWeek.SUNDAY -> "Sunday"
        }
    }

    fun getDayString(localDate: LocalDate): String {
        val today = LocalDate.now()

        return if (localDate == today) {
            "Today"
        } else {
            val dayOfWeek = localDate.dayOfWeek
            val dayOfWeekString = dayOfWeek.getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())
            dayOfWeekString
        }
    }
    fun getMonthInUpperCase(localDate: LocalDate): String {
        val month = localDate.month
        return month.getDisplayName(java.time.format.TextStyle.FULL_STANDALONE, Locale.getDefault()).uppercase()
    }

    fun formatLocalDateTime(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm", Locale.ENGLISH)
        return localDateTime.format(formatter)
    }

    private val scheduler = NotificationAlarmScheduler(context)
    fun onEvent(event: DayTaskEvent){
        when (event) {
            is DayTaskEvent.AddEventClick -> {
                addItemDropDownDialog = false
                val value = true

                sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_EVENT_SCREEN + "?eventId=NONE" + "&isEditable=true"))
                // navigate to event in edit mode

            }
            is DayTaskEvent.AddReminderClick -> {
                addItemDropDownDialog = false
                val value = true
                sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_REMINDER_SCREEN + "?reminderId=NONE" + "&isEditable=true"))
                // navigate to reminder in edit mode
            }
            is DayTaskEvent.AddTaskClick -> {
                addItemDropDownDialog = false
                val value = true
                sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_TASK_SCREEN + "?taskId=NONE" + "&isEditable=true"))
                // navigate to task in edit mode
            }
            is DayTaskEvent.ConfirmDialogOkClick -> {
                when (currItemSelected) {
                    is Event -> {
                        if ((currItemSelected as Event).isUserEventCreator) {
                            viewModelScope.launch {
                                val event1 = currItemSelected as Event
                                repository.deleteEventItem(token, event1.id)
                                val notification = repository.getNotificationById(event1.id)
                                notification?.let(scheduler::cancel)



                            }
                        }
                        else {
                            viewModelScope.launch {
                                val event1 = currItemSelected as Event
                                repository.deleteAttendee(token, event1.id)
                                val notification = repository.getNotificationById(event1.id)
                                notification?.let(scheduler::cancel)
                            }
                        }


                    }
                    is Reminder -> {
                        viewModelScope.launch {
                            val reminder1 = currItemSelected as Reminder
                            repository.deleteReminderItem(token, reminder1.id)
                            val notification = repository.getNotificationById(reminder1.id)
                            notification?.let(scheduler::cancel)

                        }

                    }
                    is Task -> {
                        viewModelScope.launch {
                            val task1 = currItemSelected as Task
                            repository.deleteTaskItem(token, task1.id)
                            val notification = repository.getNotificationById(task1.id)
                            notification?.let(scheduler::cancel)
                        }

                    }

                    // ask chatgpt if it works when an item gets deleted
                }
                deleteDialog = false
                getDayThings(LocalDateTime.of(daySelected, LocalTime.now()))
                
            }
            is DayTaskEvent.AnotherDayClick -> {
                daySelected = event.dayClicked
                getDayThings(LocalDateTime.of(daySelected, LocalTime.now()))
                sendUiEvent(UiEvent.ShowToast(""))
            }
            is DayTaskEvent.AddItemClick -> {
                addItemDropDownDialog = true

            }
            is DayTaskEvent.DeleteItemClick -> {
                deleteDialog = true



            }
            is DayTaskEvent.IconEditItemClick -> {
                editDropDownDialog = true
                currItemSelected = event.item


            }
            is DayTaskEvent.ConfirmDialogCancelClick -> {
                currItemSelected = null
                deleteDialog = false

            }
            is DayTaskEvent.LogoutClick -> {
                viewModelScope.launch {
                    repository.logout(token = token)
                }
                logoutDropDownDialog = false
                sendUiEvent(UiEvent.PopBackStack)

            }
            is DayTaskEvent.OpenAgendaDialogClick -> {
                agendaDialog = true


            }
            is DayTaskEvent.TaskItemCheckBoxClick -> {
                viewModelScope.launch {
                    repository.updateTask(token, task = event.task.toTaskEntity().copy(isDone = !event.task.isDone))
                }
                getDayThings(LocalDateTime.of(daySelected, LocalTime.now()))

            }
            is DayTaskEvent.UserProfileClick -> {
                logoutDropDownDialog = true

            }
            is DayTaskEvent.EditItemClick -> {
                when (currItemSelected) {
                    is Event -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_EVENT_SCREEN +
                                "?eventId=${(currItemSelected as Event).id}" + "&isEditable=true")  )

                    }
                    is Task -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_TASK_SCREEN +
                                "?taskId=${(currItemSelected as Task).id}" + "&isEditable=true")  )

                    }
                    is Reminder -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_REMINDER_SCREEN +
                                "?reminderId=${(currItemSelected as Reminder).id}" + "&isEditable=true")  )
                    }
                }
            }
            is DayTaskEvent.OpenItemClick -> {
                when (currItemSelected) {
                    is Event -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_EVENT_SCREEN +
                                "?eventId=${(currItemSelected as Event).id}" + "&isEditable=false")  )

                    }
                    is Task -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_TASK_SCREEN +
                                "?taskId=${(currItemSelected as Task).id}" + "&isEditable=false")  )

                    }
                    is Reminder -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_REMINDER_SCREEN +
                                "?taskId=${(currItemSelected as Reminder).id}" + "&isEditable=false")  )
                        // ask chatgpt about the smart cast
                    }
                }
            }
            is DayTaskEvent.DissmissDatePickerDialog -> {
                agendaDialog = false
            }
            is DayTaskEvent.DifferentDaySelected -> {
                daySelected = event.localDate
                getDayThings(LocalDateTime.of(daySelected, LocalTime.now()))
            }
            is DayTaskEvent.AddItemDialogDismiss -> {
                addItemDropDownDialog = false
            }
            is DayTaskEvent.EditItemsDialogDismiss -> {
                currItemSelected = null
                editDropDownDialog = false

            }
            is DayTaskEvent.LogOutDialogDismiss -> {
                logoutDropDownDialog = false

            }

        }

    }

    fun convertLocalDateTimeToLong(localDateTime: LocalDateTime): Long {
        val zoneId = getDeviceTimeZone()
        val zonedDateTime = localDateTime.atZone(zoneId)
        return zonedDateTime.toInstant().toEpochMilli()
    }


    fun getDayThings(time: LocalDateTime) {
        viewModelScope.launch {
            repository.getAgenda(token = token, timeZone = TimeZone.getDefault().id, time = convertLocalDateTimeToLong(time)).collect {resource ->
                when (resource) {
                    is Resource.Loading -> {
                        isLoading = true
                        // Handle loading state
                        delay(300)
                    }
                    is Resource.Success -> {
                        val agendaItems = resource.data
                        if (agendaItems != null) {
                            Log.d("HERE", "IT is supposed to work")
                            events = agendaItems.events
                            tasks = agendaItems.tasks
                            reminders = agendaItems.reminders
                            mergedList = (reminders + tasks + events)

                            Log.d("HERE", "this is ${mergedList.size}")
                            sortedList = mergedList.sortedBy { item ->
                                when (item) {
                                    is Reminder -> item.time // Assuming the time variable is a comparable type (e.g., Long, LocalDateTime, etc.) in Reminder class
                                    is Task -> item.time // Assuming the time variable is a comparable type (e.g., Long, LocalDateTime, etc.) in Task class
                                    is Event -> item.from // Assuming the time variable is a comparable type (e.g., Long, LocalDateTime, etc.) in Event class
                                    else -> error("Unexpected item type")
                                }
                            }

                        }

                        isLoading = false
                        // Handle the data received (e.g., update UI)
                    }
                    is Resource.Error -> {
                        val errorMessage = resource.message
                        isLoading = false
                        // Handle the error state (e.g., show error message)
                    }
                }

            }

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

    private fun getNameInitials(name: String) : String{
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

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


    private fun saveLoginResponse(loginResponse: LoginResponseDto) {
        userManager.saveLoginResponse(loginResponse)
    }

    private fun getLoginResponse(): LoginResponseDto? {
        return userManager.getLoginResponse()
    }



}