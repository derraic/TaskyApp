package com.derra.taskyapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditDescriptionScreen(
    onTextChange: () -> Unit,
    onSaveClick: () -> Unit,
    onBackClick:  () -> Unit
) {

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFFFFFFFF))
        .padding(horizontal = 17.dp) ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Box() {
                
            }
            
        }

    }


}