package com.derra.taskyapp.presentation.agenda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.mappers_dto_to_entity.getDeviceTimeZone
import com.derra.taskyapp.data.objectsviewmodel.Event
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.objectsviewmodel.Task
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
import com.derra.taskyapp.util.Resource
import com.derra.taskyapp.util.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
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
    var loginDropDowDialog by mutableStateOf(false)
    var editDropDownDialogEvent by mutableStateOf(false)
    var editDropDownDialogTask by mutableStateOf(false)
    var editDropDownDialogReminder by mutableStateOf(false)
    var addItemDropDownDialog by mutableStateOf(false)


    

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


            }
            is DayTaskEvent.AddReminderClick -> {

            }
            is DayTaskEvent.AddTaskClick -> {

            }
            is DayTaskEvent.ConfirmDialogOkClick -> {

                
            }
            is DayTaskEvent.AnotherDayClick -> {
                daySelected = event.dayClicked
                getDayThings(daySelected!!.toEpochDay())
            }
            is DayTaskEvent.AddItemClick -> {
                addItemDropDownDialog = true

            }
            is DayTaskEvent.DeleteItemClick -> {

            }
            is DayTaskEvent.EditItemClick -> {

            }
            is DayTaskEvent.ConfirmDialogCancelClick -> {

            }
            is DayTaskEvent.LogoutClick -> {

            }
            is DayTaskEvent.OpenAgendaDialogClick -> {

            }
            is DayTaskEvent.TaskItemCheckBoxClick -> {

            }
            is DayTaskEvent.UserProfileClick -> {

            }
            is DayTaskEvent.OpenItemClick -> {

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


    private fun saveLoginResponse(loginResponse: LoginResponseDto) {
        userManager.saveLoginResponse(loginResponse)
    }

    private fun getLoginResponse(): LoginResponseDto? {
        return userManager.getLoginResponse()
    }



}