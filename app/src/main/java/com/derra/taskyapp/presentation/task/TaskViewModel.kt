package com.derra.taskyapp.presentation.task


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.objectsviewmodel.Task
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
import com.derra.taskyapp.util.Resource
import com.derra.taskyapp.util.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskyRepository,
    private val userManager: UserManager,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    var task by mutableStateOf<Task?>(null)
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var time by mutableStateOf<LocalDateTime?>(null)
        private set
    var remindAt by mutableStateOf<LocalDateTime?>(null)
    var checked by mutableStateOf(false)
        private set
    var editMode by mutableStateOf(false)
        private set
    var token: String = ""



    init {
        val taskId = savedStateHandle.get<String>("taskId")!!
        val isEditable = savedStateHandle.get<Boolean>("isEditable")!!
        token = getLoginResponse()!!.token


        if (isEditable) {
            editMode = true
        }

        if (taskId != "NONE") {
            viewModelScope.launch {
                repository.getTaskItem(token, taskId).collectLatest {resource ->
                    when (resource) {
                        is Resource.Success -> {
                            task = resource.data
                            title = task?.id ?: ""
                            description = task?.description ?: ""
                            time = task?.time
                            remindAt = task?.remindAt
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

    private fun getLoginResponse(): LoginResponseDto? {
        return userManager.getLoginResponse()
    }


}