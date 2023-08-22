package com.derra.taskyapp.presentation.reminder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.derra.taskyapp.presentation.task.TaskViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ReminderEditableScreen(
    viewModel: ReminderViewModel
) {

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
            Image(painter = painterResource(id = R.drawable.cross_icon), contentDescription = "exit")
            Text(
                text = "Edit reminder",
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
        Column(modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .border(width = 1.dp, color = Color(0xFFA9B4BE), shape = RoundedCornerShape(size = 2.dp))
                    .width(20.dp)
                    .height(20.dp)
                    .background(color = Color(0xFFF2F3F7), shape = RoundedCornerShape(size = 2.dp))
                ) {
                }
                Text(
                    text = "Task",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 19.2.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(600),
                        color = Color(0xFF5C5D5A),
                    )
                )

            }
            Spacer(modifier = Modifier.height(30.5.dp))
            Row(modifier = Modifier.padding(start = 16.dp, end = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row() {

                    Image(painter = painterResource(id = R.drawable.checkbox_agendaitems_selected), contentDescription = "checked")

                    Spacer(modifier = Modifier.width(9.dp))
                    Text(
                        text = viewModel.title,
                        style = TextStyle(
                            fontSize = 26.sp,
                            lineHeight = 25.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF16161C),
                        )
                    )

                    Box(modifier = Modifier
                        .width(30.dp)
                        .height(30.35489.dp)
                        .clickable {

                        }
                    ) {
                        Image(painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right), contentDescription = "edit")

                    }
                }



            }
            Spacer(modifier = Modifier.height(22.dp))
            Modifier
                .padding(horizontal = 17.dp)
                .width(326.dp)
                .height(1.dp)
                .background(color = Color(0xFFEEF6FF))
            Row(modifier = Modifier.height(85.dp).padding(start = 17.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                Text(
                    text = viewModel.description,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF16161C),
                    )
                )

                Box(modifier = Modifier
                    .width(30.dp)
                    .height(30.35489.dp)
                    .clickable {

                    }
                ) {
                    Image(painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right), contentDescription = "edit")

                }



            }

            val time = viewModel.time?.toLocalTime()?.format(DateTimeFormatter.ofPattern("HH::mm"))
                ?: LocalTime.now().format(DateTimeFormatter.ofPattern("HH::mm"))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 17.dp)
                .height(70.00003.dp)
                .padding(start = 4.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "At",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF16161C),
                    )
                )
                Spacer(modifier = Modifier.width(39.dp))
                Text(
                    text = time,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF16161C),
                    )
                )
                Spacer(modifier = Modifier.width(82.dp))

                val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH)
                val date = viewModel.time?.toLocalDate()?.format(formatter) ?: LocalDate.now().format(formatter)
                Text(
                    text = date,
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF16161C),
                    )
                )
                Spacer(
                    modifier = Modifier
                        .padding(horizontal = 17.dp)
                        .width(326.dp)
                        .height(1.dp)
                        .background(color = Color(0xFFEEF6FF))
                )
                Column(
                    modifier = Modifier
                        .height(70.00003.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 17.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                ) {
                    Row() {
                        Image(painter = painterResource(id = R.drawable.bell_icon_in_box), contentDescription = "notification")
                        Spacer(modifier = Modifier.width(13.dp))
                        val reminderString = viewModel.time?.let { viewModel.remindAt?.let { it1 ->
                            viewModel.calculateReminderTime(it,
                                it1
                            )
                        } } ?: "30 minutes before"
                        Text(
                            modifier = Modifier.width(139.dp),
                            text = reminderString,
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 15.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF16161C),
                            )
                        )
                        Spacer(modifier = Modifier.width(127.dp))
                        Image(painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right), contentDescription = "edit?")


                    }
                    Spacer(modifier = Modifier
                        .padding(horizontal = 17.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color(0xFFEEF6FF)))
                    Spacer(modifier = Modifier.height(257.dp))
                    Spacer(modifier = Modifier
                        .padding(horizontal = 17.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color(0xFFEEF6FF)))
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { }, horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = "Delete Task",
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 30.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(600),
                                color = Color(0xFFA9B4BE),
                            )
                        )


                    }


                }


            }


        }

    }


}