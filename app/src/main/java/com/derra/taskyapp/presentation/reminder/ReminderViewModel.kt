package com.derra.taskyapp.presentation.reminder

import androidx.lifecycle.ViewModel
import com.derra.taskyapp.data.TaskyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: TaskyRepository
): ViewModel() {


}