package com.derra.taskyapp.presentation.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derra.taskyapp.R
import com.derra.taskyapp.presentation.EditDescriptionScreen
import com.derra.taskyapp.presentation.EditTitleScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EventEditableAttendeeScreen(viewModel: EventViewModel) {

    if (viewModel.editDescriptionMode) {
        EditDescriptionScreen(
            onTextChange = { newDescription -> viewModel.onEvent(EventEvent.OnTextChange(newDescription))},
            onSaveClick = { viewModel.onEvent(EventEvent.SaveNewDescriptionClick)},
            onBackClick = { viewModel.onEvent(EventEvent.OnBackButtonTextFieldClick)},
            tempString = viewModel.tempString
        )
    }
    else if (viewModel.editTitleMode) {
        EditTitleScreen(
            onTextChange = {newTitle -> viewModel.onEvent(EventEvent.OnTextChange(newTitle))},
            onSaveClick = { viewModel.onEvent(EventEvent.SaveNewTitleClick)},
            onBackClick = { viewModel.onEvent(EventEvent.OnBackButtonTextFieldClick) },
            tempString = viewModel.tempString
        )
    }
    else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFF16161C))
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .height(49.dp)
                        .padding(horizontal = 22.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val currentDate = LocalDate.now()
                    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
                    Image(modifier = Modifier.clickable { viewModel.onEvent(EventEvent.OnCrossButtonClick) },painter = painterResource(id = R.drawable.cross_icon), contentDescription = "exit")
                    Text(
                        text = currentDate.format(formatter),
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 12.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Right,
                        )
                    )
                    Text(
                        modifier = Modifier.clickable { viewModel.onEvent(EventEvent.SaveButtonClick) },
                        text = "Save",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 12.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(600),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Right,
                        )
                    )
                }


            }





        }
    }

}