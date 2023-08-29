package com.derra.taskyapp.presentation.event

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.derra.taskyapp.util.UiEvent

@Composable
fun EventScreen(
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context,
    viewModel: EventViewModel = hiltViewModel(),
            window: android.view.Window
) {

    WindowCompat.setDecorFitsSystemWindows(window, true)
    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowToast -> Toast.makeText(context,event.message,Toast.LENGTH_LONG).show()
                else -> Unit
            }
        }
    }

    if (viewModel.isHost && viewModel.editMode) {
        EventEditableHostScreen(viewModel = viewModel, window)
    }
    else if (viewModel.isHost && !viewModel.editMode) {
        EventNonEditableHostScreen(viewModel = viewModel, window)
    }
    else if (!viewModel.isHost && viewModel.editMode) {
        EventEditableAttendeeScreen(viewModel = viewModel)
    }
    else {
        EventNonEditableAttendeeScreen(viewModel = viewModel)
    }

}