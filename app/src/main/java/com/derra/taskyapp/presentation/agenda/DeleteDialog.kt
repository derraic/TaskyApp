package com.derra.taskyapp.presentation.agenda


import android.os.Build
import android.util.Log
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.paint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import com.derra.taskyapp.R
import com.derra.taskyapp.data.objectsviewmodel.Event
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.objectsviewmodel.Task


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeleteDialog(
    modifier: Modifier,
    viewModel: DayTasksViewModel,


    ) {



    if (viewModel.deleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(DayTaskEvent.ConfirmDialogCancelClick)},
            text = {
                Column(modifier = Modifier
                    .fillMaxSize()
                ) {

                    Row(modifier = Modifier
                        .width(310.dp)
                        .height(32.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically)
                    {
                        Image(painter = painterResource(id = R.drawable.cross_icon), contentDescription = "Warning")
                        Text(
                            modifier = Modifier
                                .width(245.dp)
                                .height(24.dp),
                            text = "Are you sure?",
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF232926)
                        )

                    }
                    //Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier
                        .width(350.dp)
                        .height(56.dp).
                        padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        var text = ""
                        when (viewModel.currItemSelected)  {
                            is Event -> {
                                text = (viewModel.currItemSelected as Event).title


                            }
                            is Task -> {
                                text = (viewModel.currItemSelected as Task).title

                            }
                            is Reminder -> {
                                text = (viewModel.currItemSelected as Reminder).title

                            }
                    }
                        Text(
                            modifier = Modifier
                                .width(310.dp)
                                .height(40.dp),
                            text = "Do you really want to delete $text This process cannot be undone.",
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(500),
                            color = Color(0xFF516370))
                    }
                    //Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            //.offset(x = 0.dp, y = 496.dp)
                            .width(350.dp)
                            .height(88.dp)
                        ,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(modifier = Modifier
                            //.offset(x = 20.dp, y = 16.dp)
                            .border(
                                width = 1.dp,
                                color = Color(0xFFF9F9F9),
                                shape = RoundedCornerShape(size = 10.dp)
                            )
                            .width(151.dp)
                            .height(48.dp)
                            .clickable { viewModel.onEvent(DayTaskEvent.ConfirmDialogCancelClick) }
                            .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 12.dp)
                        ) {
                            Text(
                                modifier = Modifier
                                    .width(111.dp)
                                    .height(24.dp),
                                text = "Cancel",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(500),
                                color = Color(0xFF516370),
                                textAlign = TextAlign.Center
                            )
                        }
                        Box(
                            modifier = Modifier
                                //.background(color = Color(0xFF0A6375), shape = RoundedCornerShape(size = 10.dp))
                                //.padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 12.dp)
                                .width(151.dp)
                                .height(48.dp)
                                .background(
                                    color = Color(0xFF16161C),
                                    shape = RoundedCornerShape(size = 10.dp)
                                )
                                .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 12.dp)
                                .clickable { viewModel.onEvent(DayTaskEvent.ConfirmDialogOkClick) }
                            //.paint(painter = painterResource(id = R.drawable.button_got_it_good))
                        ) {
                            Text(
                                modifier = Modifier
                                    //.offset(x = 20.dp, y = 12.dp)
                                    .width(111.dp)
                                    .height(24.dp),
                                text = "Got it",
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                fontWeight = FontWeight(500),
                                color = Color(0xFFFFFFFF),
                                textAlign = TextAlign.Center
                            )
                        }

                    }

                }
            },
            confirmButton = {
                //Button(onClick = {viewModel.onEvent(AddEditPlantEvent.SaveButtonDatesClick)}) {
                //Text(text = "Got it")
                //}
            },
            dismissButton = {
                //Button(onClick = { viewModel.onEvent(AddEditPlantEvent.CancelButtonClick) }) {
                //    Text(text = "Cancel")
                //}
            },
            modifier = Modifier
                .shadow(
                    elevation = 36.dp,
                    spotColor = Color(0x29000000),
                    ambientColor = Color(0x29000000)
                )
                .width(350.dp)
                .height(192.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 16.dp)).padding(vertical = 8.dp)

        )
    }

}