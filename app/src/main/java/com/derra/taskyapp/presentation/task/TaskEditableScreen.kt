package com.derra.taskyapp.presentation.task

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
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
import com.derra.taskyapp.presentation.agenda.DeleteItemDialog
import com.derra.taskyapp.presentation.agenda.UndoChangesDialog
import com.derra.taskyapp.presentation.reminder.ReminderEvent
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditableScreen(
    viewModel: TaskViewModel
) {

    val initialDateMillis = viewModel.date.toEpochDay() * 24 * 60 * 60 * 1000
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val initialTime = viewModel.time ?: LocalTime.now()
    if (viewModel.editDescriptionMode) {
        EditDescriptionScreen(
            onTextChange = { newDescription -> viewModel.onEvent(TaskEvent.OnTextChange(newDescription))},
            onSaveClick = { viewModel.onEvent(TaskEvent.SaveNewDescriptionClick)},
            onBackClick = { viewModel.onEvent(TaskEvent.OnBackButtonTextFieldClick)},
            tempString = viewModel.tempString
        )
    }
    else if (viewModel.editTitleMode) {
        EditTitleScreen(
            onTextChange = {newTitle -> viewModel.onEvent(TaskEvent.OnTextChange(newTitle))},
            onSaveClick = { viewModel.onEvent(TaskEvent.SaveNewTitleClick)},
            onBackClick = { viewModel.onEvent(TaskEvent.OnBackButtonTextFieldClick) },
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
                    Image(
                        modifier = Modifier.clickable { viewModel.onEvent(TaskEvent.OnCrossButtonClick) },
                        painter = painterResource(id = R.drawable.cross_icon),
                        contentDescription = "exit"
                    )
                    Text(
                        text = "Edit Task",
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
                        modifier = Modifier.clickable { viewModel.onEvent(TaskEvent.SaveButtonClick) },
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 30.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                        verticalAlignment = CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(20.dp)
                                .background(
                                    color = Color(0xFF279F70),
                                    shape = RoundedCornerShape(size = 2.dp)
                                )
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
                    Row(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = CenterVertically
                    ) {
                        Row() {
                            if (!viewModel.checked) {
                                Image(
                                    modifier = Modifier.clickable { viewModel.onEvent(TaskEvent.OnCheckedClick) },
                                    painter = painterResource(id = R.drawable.circle_checked_task),
                                    contentDescription = "unchecked circle"
                                )
                            } else {
                                Image(
                                    modifier = Modifier.clickable { viewModel.onEvent(TaskEvent.OnCheckedClick) },
                                    painter = painterResource(id = R.drawable.checkbox_agendaitems_selected),
                                    contentDescription = "checked"
                                )
                            }
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
                                    viewModel.onEvent(TaskEvent.EditTitleClick)

                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right),
                                    contentDescription = "edit"
                                )

                            }
                        }


                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    Modifier
                        .padding(horizontal = 17.dp)
                        .width(326.dp)
                        .height(1.dp)
                        .background(color = Color(0xFFEEF6FF))
                    Row(
                        modifier = Modifier
                            .height(85.dp)
                            .padding(start = 17.dp, end = 16.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

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
                                viewModel.onEvent(TaskEvent.EditDescriptionClick)
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right),
                                contentDescription = "edit"
                            )

                        }


                    }

                    val time = viewModel.time.format(DateTimeFormatter.ofPattern("HH::mm"))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 17.dp)
                            .height(70.00003.dp)
                            .padding(start = 4.dp), verticalAlignment = CenterVertically
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
                            modifier = Modifier.clickable { viewModel.onEvent(TaskEvent.EditTimeClick) },
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
                        val date =
                            viewModel.date.format(formatter) ?: LocalDate.now().format(formatter)
                        Text(
                            modifier = Modifier.clickable { viewModel.onEvent(TaskEvent.EditDateClick) },
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

                    Row(
                        modifier = Modifier
                            .height(70.00003.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 17.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bell_icon_in_box),
                            contentDescription = "notification"
                        )
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
                        Box() {
                            Image(modifier = Modifier.clickable { viewModel.onEvent(TaskEvent.AdjustNotificationClick) },
                                painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right),
                                contentDescription = "edit?")
                        }
                        DropdownMenu(
                            expanded = viewModel.reminderDropDown,
                            onDismissRequest = { viewModel.onEvent(TaskEvent.ReminderTimeDismiss) }) {
                            androidx.compose.material.DropdownMenuItem(onClick = { /*TODO*/ }) {
                                ReminderDropDown(onItemClick = { minutesBefore ->
                                    viewModel.onEvent(
                                        TaskEvent.DifferentReminderTimeClick(minutesBefore)
                                    )
                                })

                            }

                        }


                    }
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 17.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color(0xFFEEF6FF))
                    )
                    Spacer(modifier = Modifier.height(257.dp))
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 17.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color(0xFFEEF6FF))
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        Modifier
                            .fillMaxWidth(), horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            modifier = Modifier.clickable { viewModel.onEvent(TaskEvent.DeleteTaskClick) },
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



        if (viewModel.newTask && viewModel.deleteDialog) {
            UndoChangesDialog(
                modifier = Modifier,
                onCancelClick = { viewModel.onEvent(TaskEvent.DeleteCancelClick) },
                onOkClick = { viewModel.onEvent(TaskEvent.DeleteConfirmClick) },
                showDialog = true
            )
        } else if (!viewModel.newTask && viewModel.deleteDialog) {
            DeleteItemDialog(
                modifier = Modifier,
                onCancelClick = { viewModel.onEvent(TaskEvent.DeleteCancelClick) },
                onOkClick = { viewModel.onEvent(TaskEvent.DeleteConfirmClick) },
                showDialog = true
            )
        }
        val showingPicker = remember { mutableStateOf(true) }
        if (viewModel.dateDialog) {
            DatePickerDialog(onDismissRequest = { viewModel.onEvent(TaskEvent.OnDateDismiss) },
                dismissButton = {
                    TextButton(
                        onClick = {
                            viewModel.onEvent(TaskEvent.OnDateDismiss)
                        },
                        enabled = true
                    ) {
                        Text(text = "cancel")
                    }

                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedDateMillis: Long =
                                datePickerState.selectedDateMillis!! // The value from the DatePicker state

                            val selectedLocalDate =
                                LocalDate.ofEpochDay(selectedDateMillis / (24 * 60 * 60 * 1000))

                            viewModel.onEvent(TaskEvent.OnDateChange(selectedLocalDate))
                        },
                    ) {
                        Text(text = "OK")
                    }
                }) {
                DatePicker(
                    state = datePickerState
                    //, colors = DatePickerDefaults.colors()
                )

            }
        }

        val dialogState = rememberMaterialDialogState()
        MaterialDialog(
            dialogState = dialogState,
            buttons = {
                positiveButton("Ok") {
                    viewModel.onEvent(TaskEvent.OnTimeSave)
                }
                negativeButton("Cancel") {
                    viewModel.onEvent(TaskEvent.OnTimeDismiss)
                }
            }
        ) {


            timepicker(
                initialTime = initialTime,
                //colors = com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults.colors(headerTextColor = ),
                title = "select time",
                onTimeChange = { time -> viewModel.onEvent(TaskEvent.OnTimeChange(time)) }

            )

        }



        }

    }




}