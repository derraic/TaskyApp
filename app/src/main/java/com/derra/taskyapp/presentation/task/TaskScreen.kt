package com.derra.taskyapp.presentation.task

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TaskScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,

) {
    val editMode = viewModel.editMode

    if (editMode) {
        TaskEditableScreen(viewModel)

    }
    else {
        TaskNonEditableScreen(viewModel)

    }


}