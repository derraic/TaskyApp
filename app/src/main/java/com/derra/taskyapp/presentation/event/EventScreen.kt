package com.derra.taskyapp.presentation.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EventScreen(
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EventViewModel = hiltViewModel()
) {

    if (viewModel.isHost && viewModel.editMode) {
        EventEditableHostScreen(viewModel = viewModel)
    }
    else if (viewModel.isHost && !viewModel.editMode) {
        EventNonEditableHostScreen(viewModel = viewModel)
    }
    else if (!viewModel.isHost && viewModel.editMode) {
        EventEditableAttendeeScreen(viewModel = viewModel)
    }
    else {
        EventNonEditableAttendeeScreen(viewModel = viewModel)
    }

}