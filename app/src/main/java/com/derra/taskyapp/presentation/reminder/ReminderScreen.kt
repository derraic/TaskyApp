package com.derra.taskyapp.presentation.reminder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.derra.taskyapp.presentation.task.TaskEditableScreen
import com.derra.taskyapp.presentation.task.TaskNonEditableScreen
import com.derra.taskyapp.util.UiEvent

@Composable
fun ReminderScreen(
    viewModel: ReminderViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    window: android.view.Window
) {

    WindowCompat.setDecorFitsSystemWindows(window, true)

    val editMode = viewModel.editMode

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                else -> Unit
            }
        }
    }

    if (editMode) {
        ReminderEditableScreen(viewModel)

    }
    else {
        ReminderNonEditableScreen(viewModel)

    }


}