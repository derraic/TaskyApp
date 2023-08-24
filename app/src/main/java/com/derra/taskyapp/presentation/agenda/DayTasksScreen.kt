package com.derra.taskyapp.presentation.agenda

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.material3.TextButton


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.derra.taskyapp.util.UiEvent
import com.derra.taskyapp.R
import com.derra.taskyapp.data.objectsviewmodel.Event
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.objectsviewmodel.Task
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DayTasksScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: DayTasksViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit,
){
    val scaffoldState = rememberScaffoldState()


    val initialDateMillis = if (viewModel.daySelected != null) {
        viewModel.daySelected!!.toEpochDay() * 24 * 60 * 60 * 1000 // Convert days to milliseconds
    } else {
        LocalDate.now().toEpochDay() * 24 * 60 * 60 * 1000
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                is UiEvent.ShowSnackBar -> {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.action
                    )
                }
                is UiEvent.Navigate -> {
                    onNavigate(event)

                }
            }
        }
    }

    val currentDateAndTime = LocalDateTime.now()
    val selectedDateAndTimeMillis: Long = datePickerState.selectedDateMillis!!

    val selectedLocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(selectedDateAndTimeMillis), ZoneId.systemDefault())

    val isDateAndTimeAfterNow = selectedLocalDateTime.isAfter(currentDateAndTime)



    Scaffold(scaffoldState = scaffoldState, modifier = Modifier.fillMaxSize(), floatingActionButton = {
        Box(modifier = Modifier.clickable {viewModel.onEvent(DayTaskEvent.UserProfileClick)  }){
            Image(painter = painterResource(id = R.drawable.black_box_add_item), contentDescription = "black box")
            Image(painter = painterResource(id = R.drawable.plus_add_item), contentDescription = "plus icon")
        }
        DropdownMenu(expanded = viewModel.addItemDropDownDialog , onDismissRequest = { viewModel.onEvent(DayTaskEvent.AddItemClick)}) {
            DropdownMenuItem(onClick = {}) {
                AddAgendaDropDown(viewModel)
            }

        }
    }) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0xFF16161C))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(53.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(53.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row() {
                            Text(
                                text = viewModel.getMonthInUpperCase(viewModel.daySelected!!),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 19.2.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFFFFFFFF),
                                )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Image(
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(DayTaskEvent.OpenAgendaDialogClick)
                                },
                                painter = painterResource(id = R.drawable.drop_down_button),
                                contentDescription = "choose different datum"
                            )


                        }
                        Box(contentAlignment = Alignment.Center,
                            modifier = Modifier.clickable { viewModel.onEvent(DayTaskEvent.UserProfileClick) }) {
                            Image(
                                painter = painterResource(id = R.drawable.profile_rounded_icon),
                                contentDescription = "profile icon"
                            )
                            Text(
                                text = viewModel.name.uppercase(Locale.ROOT),
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    lineHeight = 15.6.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(600),
                                    color = Color(0xFFB7C6DE),
                                )
                            )

                        }
                        DropdownMenu(
                            expanded = viewModel.logoutDropDownDialog,
                            onDismissRequest = { viewModel.onEvent(DayTaskEvent.LogOutDialogDismiss) }) {
                            DropdownMenuItem(onClick = { viewModel.onEvent(DayTaskEvent.LogoutClick) }) {
                                LogoutDialog()
                            }

                        }

                    }

                }
                Row(
                    modifier = Modifier
                        .width(360.dp)
                        .height(90.dp)
                        .background(
                            color = Color(0xFFFFFFFF),
                            shape = RoundedCornerShape(
                                topStart = 30.dp,
                                topEnd = 30.dp,
                                bottomEnd = 0.dp,
                                bottomStart = 0.dp
                            )
                        )
                        .padding(horizontal = 22.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val daySelected = viewModel.daySelected!!
                    DayItem(
                        isSelected = true,
                        weekDayInitial = viewModel.getDayOfWeek(daySelected).take(1),
                        dayInitial = daySelected.dayOfMonth.toString()
                    )
                    DayItem(
                        isSelected = false,
                        weekDayInitial = viewModel.getDayOfWeek(daySelected.plusDays(1)),
                        dayInitial = daySelected.plusDays(1).dayOfMonth.toString()
                    )
                    DayItem(
                        isSelected = false,
                        weekDayInitial = viewModel.getDayOfWeek(daySelected.plusDays(2)),
                        dayInitial = daySelected.plusDays(2).dayOfMonth.toString()
                    )
                    DayItem(
                        isSelected = false,
                        weekDayInitial = viewModel.getDayOfWeek(daySelected.plusDays(3)),
                        dayInitial = daySelected.plusDays(3).dayOfMonth.toString()
                    )
                    DayItem(
                        isSelected = false,
                        weekDayInitial = viewModel.getDayOfWeek(daySelected.plusDays(4)),
                        dayInitial = daySelected.plusDays(4).dayOfMonth.toString()
                    )
                    DayItem(
                        isSelected = false,
                        weekDayInitial = viewModel.getDayOfWeek(daySelected.plusDays(5)),
                        dayInitial = daySelected.plusDays(5).dayOfMonth.toString()
                    )

                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = viewModel.getDayString(viewModel.daySelected!!),
                    modifier = Modifier.padding(start = 15.dp),
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 16.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFF16161C),
                    )
                )
                Spacer(modifier = Modifier.height(23.dp))


                LazyColumn() {
                    var addLine = true
                    var addSpace = false
                    items(viewModel.sortedList) { item ->
                        when (item) {
                            is Reminder -> {
                                if (item.time > LocalDateTime.now() && addLine) {
                                    addLine = false
                                    addSpace = false
                                    Spacer(modifier = Modifier.height(6.dp))
                                    TimeArrowIndicator()
                                    Spacer(modifier = Modifier.height(7.dp))

                                } else {
                                    addSpace = true
                                }
                                ReminderItem(
                                    reminder = item,
                                    dateTime = viewModel.formatLocalDateTime(item.time),
                                    viewModel
                                )
                                if (addSpace) {
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                            is Event -> {
                                if (item.from > LocalDateTime.now() && addLine) {
                                    addLine = false
                                    addSpace = false
                                    Spacer(modifier = Modifier.height(6.dp))
                                    TimeArrowIndicator()
                                    Spacer(modifier = Modifier.height(7.dp))

                                } else {
                                    addSpace = true
                                }
                                EventItem(
                                    event = item,
                                    startDateTime = viewModel.formatLocalDateTime(item.from),
                                    endDateTime = viewModel.formatLocalDateTime(item.to),
                                    viewModel
                                )
                                if (addSpace) {
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                            is Task -> {
                                if (item.time > LocalDateTime.now() && addLine) {
                                    addLine = false
                                    addSpace = false
                                    Spacer(modifier = Modifier.height(6.dp))
                                    TimeArrowIndicator()
                                    Spacer(modifier = Modifier.height(7.dp))

                                } else {
                                    addSpace = true
                                }
                                TaskItem(
                                    task = item,
                                    dateTime = viewModel.formatLocalDateTime(item.time),
                                    viewModel
                                )
                                if (addSpace) {
                                    Spacer(modifier = Modifier.height(15.dp))
                                }
                            }
                            // Composable to display a Task
                        }
                    }


                }
            }
            if (viewModel.agendaDialog) {
                DatePickerDialog(onDismissRequest = {viewModel.onEvent(DayTaskEvent.DissmissDatePickerDialog) },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.onEvent(DayTaskEvent.DissmissDatePickerDialog)
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

                                viewModel.onEvent(DayTaskEvent.DifferentDaySelected(selectedLocalDate))
                            },
                            enabled = isDateAndTimeAfterNow
                        ) {
                            androidx.compose.material3.Text(text = "OK")
                        }
                    }) {
                    DatePicker(state = datePickerState
                        //, colors = DatePickerDefaults.colors()
                    )

                }
            }
            DeleteDialog(modifier = Modifier, viewModel = viewModel)

        }


        }


}