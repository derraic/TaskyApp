package com.derra.taskyapp.presentation.reminder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.derra.taskyapp.presentation.task.TaskEditableScreen
import com.derra.taskyapp.presentation.task.TaskNonEditableScreen

@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val editMode = viewModel.editMode

    if (editMode) {
        ReminderEditableScreen(viewModel)

    }
    else {
        ReminderEditableScreen(viewModel)

    }


}