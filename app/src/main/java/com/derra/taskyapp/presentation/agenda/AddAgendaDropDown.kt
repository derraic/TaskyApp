package com.derra.taskyapp.presentation.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derra.taskyapp.R


@Composable
fun AddAgendaDropDown(viewModel: DayTasksViewModel) {
    Column (modifier = Modifier
        .shadow(elevation = 4.dp, spotColor = Color(0x1A000000), ambientColor = Color(0x1A000000))
        .width(180.dp)
        .height(165.dp)
        .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 7.dp))) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clickable { viewModel.onEvent(DayTaskEvent.AddEventClick) }
            .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Event", style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF16161C),
                )
            )

        }
        Spacer(modifier = Modifier
            .padding(0.dp)
            .width(202.dp)
            .height(1.dp)
            .background(color = Color(0xFFEEF6FF)))

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .clickable { viewModel.onEvent(DayTaskEvent.AddTaskClick) }
            .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Task",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF16161C),
                )
            )

        }
        Spacer(modifier = Modifier
            .padding(0.dp)
            .width(202.dp)
            .height(1.dp)
            .background(color = Color(0xFFEEF6FF)))
        Row(modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.onEvent(DayTaskEvent.AddReminderClick) }
            .height(55.dp)
            .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminder",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFF16161C),
                )
            )

        }
    }


}