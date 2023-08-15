package com.derra.taskyapp.presentation.event

import androidx.lifecycle.ViewModel
import com.derra.taskyapp.data.TaskyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: TaskyRepository
) : ViewModel() {

}