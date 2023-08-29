package com.derra.taskyapp.presentation.reminder


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.derra.taskyapp.presentation.ReminderDropDown
import com.derra.taskyapp.presentation.agenda.DayTaskEvent
import com.derra.taskyapp.presentation.agenda.DeleteItemDialog
import com.derra.taskyapp.presentation.agenda.UndoChangesDialog
import com.derra.taskyapp.presentation.event.ReminderDropdownReal
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ReminderEditableScreen(
    viewModel: ReminderViewModel
) {

    val initialDateMillis = viewModel.date.toEpochDay() * 24 * 60 * 60 * 1000
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val initialTime = viewModel.time ?: LocalTime.now()
    val dialogState = rememberMaterialDialogState()
    if (viewModel.editDescriptionMode) {
        EditDescriptionScreen(
            onTextChange = { newDescription -> viewModel.onEvent(ReminderEvent.OnTextChange(newDescription))},
            onSaveClick = { viewModel.onEvent(ReminderEvent.SaveNewDescriptionClick)},
            onBackClick = { viewModel.onEvent(ReminderEvent.OnBackButtonTextFieldClick)},
            tempString = viewModel.tempString
        )
    }
    else if (viewModel.editTitleMode) {
        EditTitleScreen(
            onTextChange = {newTitle -> viewModel.onEvent(ReminderEvent.OnTextChange(newTitle))},
            onSaveClick = { viewModel.onEvent(ReminderEvent.SaveNewTitleClick)},
            onBackClick = { viewModel.onEvent(ReminderEvent.OnBackButtonTextFieldClick) },
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
                    Image(modifier = Modifier.clickable { viewModel.onEvent(ReminderEvent.OnCrossButtonClick) },painter = painterResource(id = R.drawable.cross_icon), contentDescription = "exit")
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
                        modifier = Modifier.clickable { viewModel.onEvent(ReminderEvent.SaveButtonClick) },
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
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFFA9B4BE),
                                    shape = RoundedCornerShape(size = 2.dp)
                                )
                                .width(20.dp)
                                .height(20.dp)
                                .background(
                                    color = Color(0xFFF2F3F7),
                                    shape = RoundedCornerShape(size = 2.dp)
                                )
                        ) {
                        }
                        Text(
                            text = "Reminder",
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
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 17.dp, end = 17.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Image(
                                painter = painterResource(id = R.drawable.circle_checked_task),
                                contentDescription = "checked"
                            )

                            Spacer(modifier = Modifier.width(9.dp))
                            Text(
                                text = if (viewModel.title.isBlank()) "Title" else viewModel.title,
                                style = TextStyle(
                                    fontSize = 26.sp,
                                    lineHeight = 25.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = if (viewModel.title.isBlank()) FontWeight(700) else FontWeight(500),
                                    color = if (viewModel.title.isBlank()) Color(0xFFA9B4BE) else Color(
                                        0xFF16161C
                                    ),
                                )
                            )
                        }

                            Box(modifier = Modifier
                                .width(30.dp)
                                .clickable {
                                    viewModel.onEvent(ReminderEvent.EditTitleClick)
                                }
                            ) {
                                Image(painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right), contentDescription = "edit")

                            }




                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    Spacer(modifier = Modifier
                        .padding(horizontal = 17.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(color = Color(0xFFEEF6FF)))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 17.dp, end = 17.dp, bottom = 20.dp, top = 17.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

                        Text(
                            text = if (viewModel.description.isBlank()) "Description" else viewModel.description,
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 15.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = if (viewModel.description.isBlank()) Color(0xFFA9B4BE) else Color(0xFF16161C),
                            )
                        )

                        Box(modifier = Modifier
                            .width(30.dp)
                            .clickable {
                                viewModel.onEvent(ReminderEvent.EditDescriptionClick)
                            }
                        ) {
                            Image(painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right), contentDescription = "edit")

                        }



                    }

                    val time = viewModel.dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 17.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color(0xFFEEF6FF))
                    )

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 17.dp, end = 30.dp)
                        .height(70.dp)
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
                            modifier = Modifier.clickable { viewModel.onEvent(ReminderEvent.EditTimeClick)
                                                          dialogState.show()},
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
                        val date = viewModel.dateTime.toLocalDate().format(formatter)
                        Text(
                            modifier = Modifier.clickable { viewModel.onEvent(ReminderEvent.EditDateClick) },
                            text = date,
                            style = TextStyle(
                                fontSize = 16.sp,
                                lineHeight = 15.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF16161C),
                            )
                        )
                    }
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 17.dp)
                                .width(326.dp)
                                .height(1.dp)
                                .background(color = Color(0xFFEEF6FF))
                        )

                        Row(modifier = Modifier
                            .height(70.dp)
                            .fillMaxWidth()
                            .padding(start = 17.dp, end = 30.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,) {
                            Image(painter = painterResource(id = R.drawable.bell_icon_in_box), contentDescription = "notification")
                            Spacer(modifier = Modifier.width(13.dp))

                            Text(
                                modifier = Modifier.width(139.dp),
                                text = viewModel.giveReminderString(viewModel.minutesBefore),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF16161C),
                                )
                            )
                            Spacer(modifier = Modifier.width(127.dp))
                            Column() {
                                Box (modifier = Modifier
                                    .width(30.dp)
                                    .height(30.dp)
                                , contentAlignment = Alignment.Center) {
                                    Image(modifier = Modifier.clickable { viewModel.onEvent(ReminderEvent.AdjustNotificationClick) }
                                        ,painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right), contentDescription = "edit?")
                                }
                                ReminderDropdownReal(
                                    expanded = viewModel.reminderDropDown,
                                    onDissmissRequest = { viewModel.onEvent(ReminderEvent.ReminderTimeDismiss)},
                                    onItemClick = {minutesBefore -> viewModel.onEvent(ReminderEvent.DifferentReminderTimeClick(minutesBefore))}
                                )

                            }




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
                                , horizontalArrangement = Arrangement.Center) {
                            Text(
                                modifier = Modifier.clickable { viewModel.onEvent(ReminderEvent.DeleteReminderClick) },
                                text = "DELETE TASK",
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

            if (viewModel.newReminder && viewModel.deleteDialog) {
                UndoChangesDialog(
                    modifier = Modifier,
                    onCancelClick = { viewModel.onEvent(ReminderEvent.DeleteCancelClick)  },
                    onOkClick = { viewModel.onEvent(ReminderEvent.DeleteConfirmClick) },
                    showDialog = true
                )
            }
            else if (!viewModel.newReminder && viewModel.deleteDialog) {
                DeleteItemDialog(
                    modifier = Modifier,
                    onCancelClick = { viewModel.onEvent(ReminderEvent.DeleteCancelClick) },
                    onOkClick = { viewModel.onEvent(ReminderEvent.DeleteConfirmClick) },
                    showDialog = true
                )
            }
            val showingPicker = remember { mutableStateOf(true) }
            if (viewModel.dateDialog) {
                DatePickerDialog(onDismissRequest = { viewModel.onEvent(ReminderEvent.OnDateDismiss) },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.onEvent(ReminderEvent.OnDateDismiss)
                            },
                            enabled = true
                        ) {
                            androidx.compose.material3.Text(text = "cancel")
                        }

                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedDateMillis: Long = datePickerState.selectedDateMillis!! // The value from the DatePicker state

                                val selectedLocalDate = LocalDate.ofEpochDay(selectedDateMillis / (24 * 60 * 60 * 1000))

                                viewModel.onEvent(ReminderEvent.OnDateChange(selectedLocalDate))
                            },
                        ) {
                            androidx.compose.material3.Text(text = "OK")
                        }
                    }) {
                    DatePicker(state = datePickerState
                        //, colors = DatePickerDefaults.colors()
                    )

                }
            }



            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton("Ok") {
                        viewModel.onEvent(ReminderEvent.OnTimeSave)
                        dialogState.hide()
                    }
                    negativeButton("Cancel") {
                        viewModel.onEvent(ReminderEvent.OnTimeDismiss)
                        dialogState.hide()
                    }
                }
            ) {


                timepicker (
                    initialTime = initialTime,
                    //colors = com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults.colors(headerTextColor = ),
                    title = "select time",
                    onTimeChange = { time -> viewModel.onEvent(ReminderEvent.OnTimeChange(time))}

                        )

            }



        }

    }



}