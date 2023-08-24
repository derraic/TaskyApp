package com.derra.taskyapp.presentation.event

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
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
fun EventEditableHostScreen(viewModel: EventViewModel) {

    val initialDateMillis = viewModel.fromDate.toEpochDay() * 24 * 60 * 60 * 1000
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val initialTime = viewModel.fromTime ?: LocalTime.now()

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
                    Image(
                        modifier = Modifier.clickable { viewModel.onEvent(EventEvent.OnCrossButtonClick) },
                        painter = painterResource(id = R.drawable.cross_icon),
                        contentDescription = "exit"
                    )
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

                LazyColumn() {
                    this.item {

                        Row(
                            modifier = Modifier.padding(start = 16.dp, top = 30.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier =
                                Modifier
                                    .width(20.dp)
                                    .height(20.dp)
                                    .background(
                                        color = Color(0xFFCAEF45),
                                        shape = RoundedCornerShape(size = 2.dp)
                                    )
                            ) {
                            }
                            Text(
                                text = "Event",
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row() {

                                Image(
                                    painter = painterResource(id = R.drawable.circle_checked_task),
                                    contentDescription = "checked"
                                )

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
                                        viewModel.onEvent(EventEvent.EditTitleClick)
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
                                .height(87.dp)
                                .padding(start = 17.dp, end = 16.dp, top = 17.dp),
                            verticalAlignment = Alignment.Top,
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
                                    viewModel.onEvent(EventEvent.EditDescriptionClick)
                                }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.edit_mode_screen_icon_arrow_to_the_right),
                                    contentDescription = "edit"
                                )

                            }


                        }
                        if (viewModel.photos.isEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(109.dp)
                                    .background(
                                        color = Color(0xFFF2F3F7)
                                    )
                                    .padding(start = 104.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(modifier = Modifier.clickable {
                                    TODO()
                                }) {
                                    Box(
                                        modifier = Modifier
                                            .width(30.dp)
                                            .height(30.dp), contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.plus_icon_add_photos),
                                            contentDescription = "add"
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.5.dp))
                                    Text(
                                        text = "Add photos",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            lineHeight = 18.sp,
                                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                                            fontWeight = FontWeight(600),
                                            color = Color(0xFFA9B4BE),
                                        )
                                    )

                                }

                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(141.dp)
                                    .background(color = Color(0xFFF2F3F7))
                                    .padding(start = 17.dp, top = 21.dp)
                            ) {
                                Text(
                                    text = "Photos",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        lineHeight = 18.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(600),
                                        color = Color(0xFF16161C),
                                    )
                                )
                                Spacer(Modifier.height(21.dp))
                                LazyRow(modifier = Modifier.height(60.dp)) {

                                }


                            }

                            Spacer(modifier = Modifier.height(25.dp))

                        }
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 17.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(color = Color(0xFFEEF6FF))
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 17.dp)
                                .height(70.00003.dp)
                                .padding(start = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "From",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF16161C),
                                )
                            )
                            Spacer(modifier = Modifier.width(39.dp))
                            val time = viewModel.fromDateTime.toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH::mm"))
                            Text(
                                modifier = Modifier.clickable { viewModel.onEvent(EventEvent.EditFromTimeClick) },
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

                            val formatter =
                                DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH)
                            val date = viewModel.fromDateTime.toLocalDate().format(formatter)
                            Text(
                                modifier = Modifier.clickable { viewModel.onEvent(EventEvent.EditFromDateClick) },
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
                                .fillMaxWidth()
                                .padding(horizontal = 17.dp)
                                .height(70.00003.dp)
                                .padding(start = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "To",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF16161C),
                                )
                            )
                            Spacer(modifier = Modifier.width(39.dp))
                            val toTime = viewModel.toDateTime.toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH::mm"))
                            Text(
                                modifier = Modifier.clickable { viewModel.onEvent(EventEvent.EditFromTimeClick) },
                                text = toTime,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF16161C),
                                )
                            )
                            Spacer(modifier = Modifier.width(82.dp))

                            val formatter =
                                DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH)
                            val toDate = viewModel.fromDateTime.toLocalDate().format(formatter)
                            Text(
                                modifier = Modifier.clickable { viewModel.onEvent(EventEvent.EditFromDateClick) },
                                text = toDate,
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
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(color = Color(0xFFEEF6FF))
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(35.dp)
                            .padding(start = 17.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Visitors",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    lineHeight = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFF16161C),
                                )
                            )
                            Spacer(modifier = Modifier.width(18.dp))
                            Box(modifier = Modifier
                                .width(35.dp)
                                .height(35.dp)
                                .background(
                                    color = Color(0xFFF2F3F7),
                                    shape = RoundedCornerShape(size = 5.dp)
                                ),
                                contentAlignment = Alignment.Center) {
                                Image(painter = painterResource(id = R.drawable.plus_icon_add_visitors), contentDescription = "Add")


                            }


                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .padding(horizontal = 17.dp)) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(30.dp)
                                    .clickable { TODO() }
                                    .background(
                                        color = if (viewModel.visitorsButtonSelected == 0) Color(
                                            0xFF16161C
                                        ) else Color(0xFFF2F3F7),
                                        shape = RoundedCornerShape(size = 100.dp)
                                    )
                                , contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "All",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 15.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(500),
                                        color = Color(0xFFFFFFFF),
                                    )
                                )

                            }
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(30.dp)
                                    .clickable { TODO() }
                                    .background(
                                        color = if (viewModel.visitorsButtonSelected == 1) Color(
                                            0xFF16161C
                                        ) else Color(0xFFF2F3F7),
                                        shape = RoundedCornerShape(size = 100.dp)
                                    )
                                , contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Going",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 15.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(500),
                                        color = Color(0xFFFFFFFF),
                                    )
                                )

                            }
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(30.dp)
                                    .clickable { TODO() }
                                    .background(
                                        color = if (viewModel.visitorsButtonSelected == 2) Color(
                                            0xFF16161C
                                        ) else Color(0xFFF2F3F7),
                                        shape = RoundedCornerShape(size = 100.dp)
                                    )
                                , contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Not Going",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        lineHeight = 15.sp,
                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                        fontWeight = FontWeight(500),
                                        color = Color(0xFFFFFFFF),
                                    )
                                )

                            }

                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 18.dp)) {
                            Text(
                                text = if (viewModel.visitorsButtonSelected != 2 )"Going" else "Not going",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(500),
                                    color = Color(0xFF5C5D5A),
                                )
                            )

                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        viewModel.attendees.
                        filter { attendee -> if (viewModel.visitorsButtonSelected != 2) attendee.isGoing else !attendee.isGoing }
                            .forEach { it ->
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .paint(
                                        painter = painterResource(
                                            id = R.drawable.visitors_box

                                        )
                                    ).padding(start = 10.dp), verticalAlignment = Alignment.CenterVertically) {







                                    
                                }
                            }

                        

                        










                    }
                }


            }

            if (viewModel.newEvent && viewModel.deleteDialog) {
                UndoChangesDialog(
                    modifier = Modifier,
                    onCancelClick = { viewModel.onEvent(EventEvent.DeleteCancelClick)  },
                    onOkClick = { viewModel.onEvent(EventEvent.DeleteConfirmClick) },
                    showDialog = true
                )
            }
            else if (!viewModel.newEvent && viewModel.deleteDialog) {
                DeleteItemDialog(
                    modifier = Modifier,
                    onCancelClick = { viewModel.onEvent(EventEvent.DeleteCancelClick) },
                    onOkClick = { viewModel.onEvent(EventEvent.DeleteConfirmClick) },
                    showDialog = true
                )
            }
            val showingPicker = remember { mutableStateOf(true) }
            if (viewModel.dateDialog) {
                DatePickerDialog(onDismissRequest = { viewModel.onEvent(EventEvent.OnDateDismiss) },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.onEvent(EventEvent.OnDateDismiss)
                            },
                            enabled = true
                        ) {
                            Text(text = "cancel")
                        }

                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val selectedDateMillis: Long = datePickerState.selectedDateMillis!! // The value from the DatePicker state

                                val selectedLocalDate = LocalDate.ofEpochDay(selectedDateMillis / (24 * 60 * 60 * 1000))

                                viewModel.onEvent(EventEvent.OnDateChange(selectedLocalDate))
                            },
                        ) {
                            Text(text = "OK")
                        }
                    }) {
                    DatePicker(state = datePickerState
                        //, colors = DatePickerDefaults.colors()
                    )

                }
            }


            val dialogState = rememberMaterialDialogState()
            MaterialDialog(
                dialogState = dialogState,
                buttons = {
                    positiveButton("Ok") {
                        viewModel.onEvent(EventEvent.OnTimeSave)
                    }
                    negativeButton("Cancel") {
                        viewModel.onEvent(EventEvent.OnTimeDismiss)
                    }
                }
            ) {


                timepicker (
                    initialTime = initialTime,
                    //colors = com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults.colors(headerTextColor = ),
                    title = "select time",
                    onTimeChange = { time -> viewModel.onEvent(EventEvent.OnTimeChange(time))}

                )

            }





            }
        }


}