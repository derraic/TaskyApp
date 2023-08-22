package com.derra.taskyapp.presentation.agenda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.mappers.toTaskEntity
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DayTasksViewModel @Inject constructor(
    private val repository: TaskyRepository,
    private val userManager: UserManager
): ViewModel(){


    private var userInfo: LoginResponseDto? = null
    var name by mutableStateOf("")
    var daySelected by mutableStateOf<LocalDate?>(null)
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


    

    var mergedList by mutableStateOf<List<Any>>(reminders + tasks + events)
    init {

        userInfo = getLoginResponse()
        token = userInfo?.token ?: ""
        name = userInfo?.fullName ?: ""
        name = getNameInitials(name)
        daySelected = LocalDate.now()
        getDayThings(LocalDate.now().toEpochDay())



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

    fun onEvent(event: DayTaskEvent){
        when (event) {
            is DayTaskEvent.AddEventClick -> {
                addItemDropDownDialog = false

                sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_EVENT_SCREEN))
                // navigate to event in edit mode

            }
            is DayTaskEvent.AddReminderClick -> {
                addItemDropDownDialog = false
                sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_REMINDER_SCREEN))
                // navigate to reminder in edit mode
            }
            is DayTaskEvent.AddTaskClick -> {
                addItemDropDownDialog = false
                sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_TASK_SCREEN))
                // navigate to task in edit mode
            }
            is DayTaskEvent.ConfirmDialogOkClick -> {
                when (currItemSelected) {
                    is Event -> {
                        viewModelScope.launch {
                            repository.deleteEventItem(token, (currItemSelected as Event).id)
                        }

                    }
                    is Reminder -> {
                        viewModelScope.launch {
                            repository.deleteReminderItem(token, (currItemSelected as Reminder).id)
                        }

                    }
                    is Task -> {
                        viewModelScope.launch {
                            repository.deleteTaskItem(token, (currItemSelected as Task).id)
                        }

                    }

                    // ask chatgpt if it works when an item gets deleted
                }
                getDayThings(daySelected!!.toEpochDay())
                
            }
            is DayTaskEvent.AnotherDayClick -> {
                daySelected = event.dayClicked
                getDayThings(daySelected!!.toEpochDay())
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
                    repository.updateTask(token, task = event.task.toTaskEntity().copy(isDone = event.checked))
                }
               getDayThings(daySelected!!.toEpochDay())

            }
            is DayTaskEvent.UserProfileClick -> {
                logoutDropDownDialog = true

            }
            is DayTaskEvent.EditItemClick -> {
                when (currItemSelected) {
                    is Event -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_EVENT_SCREEN +
                                "?eventId=${(currItemSelected as Event).id}" + "?isEditable={true}")  )

                    }
                    is Task -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_TASK_SCREEN +
                                "?taskId=${(currItemSelected as Task).id}" + "?isEditable={true}")  )

                    }
                    is Reminder -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_REMINDER_SCREEN +
                                "?taskId=${(currItemSelected as Reminder).id}" + "?isEditable={true}")  )
                    }
                }
            }
            is DayTaskEvent.OpenItemClick -> {
                when (currItemSelected) {
                    is Event -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_EVENT_SCREEN +
                                "?eventId=${(currItemSelected as Event).id}" + "?isEditable={false}")  )

                    }
                    is Task -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_TASK_SCREEN +
                                "?taskId=${(currItemSelected as Task).id}" + "?isEditable={false}")  )

                    }
                    is Reminder -> {
                        sendUiEvent(UiEvent.Navigate(Routes.EDIT_DETAIL_REMINDER_SCREEN +
                                "?taskId=${(currItemSelected as Reminder).id}" + "?isEditable={false}")  )
                        // ask chatgpt about the smart cast
                    }
                }
            }
            is DayTaskEvent.DissmissDatePickerDialog -> {
                agendaDialog = false
            }
            is DayTaskEvent.DifferentDaySelected -> {
                daySelected = event.localDate
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


    private fun getDayThings(time: Long) {
        viewModelScope.launch {
            repository.getAgenda(token = token, timeZone = TimeZone.getDefault().id, time = time).collectLatest {resource ->
                when (resource) {
                    is Resource.Loading -> {
                        isLoading = true
                        // Handle loading state
                    }
                    is Resource.Success -> {
                        val agendaItems = resource.data
                        if (agendaItems != null) {
                            events = agendaItems.events
                            tasks = agendaItems.tasks
                            reminders = agendaItems.reminders
                            mergedList = (reminders + tasks + events)

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