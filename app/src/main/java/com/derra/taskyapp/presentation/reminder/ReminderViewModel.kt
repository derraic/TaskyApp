package com.derra.taskyapp.presentation.reminder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.objectsviewmodel.Task
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
import com.derra.taskyapp.util.Resource
import com.derra.taskyapp.util.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: TaskyRepository,
    private val userManager: UserManager,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var task by mutableStateOf<Reminder?>(null)
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var time by mutableStateOf<LocalDateTime?>(null)
        private set
    var remindAt by mutableStateOf<LocalDateTime?>(null)
    var editMode by mutableStateOf(false)
        private set
    var token: String = ""



    init {
        val reminderId = savedStateHandle.get<String>("reminderId")!!
        val isEditable = savedStateHandle.get<Boolean>("isEditable")!!
        token = getLoginResponse()!!.token


        if (isEditable) {
            editMode = true
        }

        if (reminderId != "NONE") {
            viewModelScope.launch {
                repository.getReminderItem(token,reminderId ).collectLatest {resource ->
                    when (resource) {
                        is Resource.Success -> {
                            task = resource.data
                            title = task?.id ?: ""
                            description = task?.description ?: ""
                            time = task?.time
                            remindAt = task?.remindAt


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

    fun calculateReminderTime(dateTime: LocalDateTime, remindAt: LocalDateTime): String {
        val duration = Duration.between(dateTime, remindAt)

        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()

        return when {
            minutes < 10 -> "$minutes minutes before"
            minutes < 30 -> "$minutes minutes before"
            hours < 1 -> "$minutes minutes before"
            hours < 6 -> "$hours hours before"
            days < 1 -> "$hours hours before"
            else -> "$days days before"
        }
    }

    private fun getLoginResponse(): LoginResponseDto? {
        return userManager.getLoginResponse()
    }




}