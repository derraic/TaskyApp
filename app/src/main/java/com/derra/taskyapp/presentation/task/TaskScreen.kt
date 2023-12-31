package com.derra.taskyapp.presentation.task

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.derra.taskyapp.util.UiEvent


@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    window: android.view.Window
) {

    WindowCompat.setDecorFitsSystemWindows(window, true)


    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                else -> Unit
            }
        }
    }
    val editMode = viewModel.editMode

    if (editMode) {
        TaskEditableScreen(viewModel)

    }
    else {
        TaskNonEditableScreen(viewModel)

    }


}