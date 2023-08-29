package com.derra.taskyapp.presentation.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.rememberAsyncImagePainter
import com.derra.taskyapp.R
import com.derra.taskyapp.presentation.EditDescriptionScreen
import com.derra.taskyapp.presentation.EditTitleScreen
import com.derra.taskyapp.presentation.ReminderDropDown
import com.derra.taskyapp.presentation.reminder.ReminderEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun EventNonEditableHostScreen(
    viewModel: EventViewModel,
    window: android.view.Window
) {

    WindowCompat.setDecorFitsSystemWindows(window, true)

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
    else if (viewModel.photoScreenOpen){
        PhotoDetailScreen(viewModel = viewModel, photo = viewModel.photoToOpen!!, window)
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
                    Image(modifier = Modifier.clickable { viewModel.onEvent(EventEvent.EditIconClick) }, painter = painterResource(id = R.drawable.edit_pencil_icon), contentDescription = "edit")

                }

                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xFFFFFFFF),
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                    )) {
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


                        }
                        Spacer(modifier = Modifier.height(22.dp))
                        Spacer(modifier = Modifier
                            .padding(horizontal = 17.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color(0xFFEEF6FF)))
                        Row(
                            modifier = Modifier
                                .height(87.dp)
                                .padding(start = 17.dp, end = 16.dp, top = 17.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

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
                                    items(viewModel.photos) {photo ->
                                        Box(modifier = Modifier
                                            .border(
                                                width = 3.dp,
                                                color = Color(0xFFB7C6DE),
                                                shape = RoundedCornerShape(size = 5.dp)
                                            )
                                            .clickable {
                                                viewModel.onEvent(
                                                    EventEvent.PhotoScreenOpenClick(
                                                        photo
                                                    )
                                                )
                                            }
                                            .padding(1.5.dp)
                                            .width(60.dp)
                                            .height(60.dp)) {

                                            if (photo.key.startsWith("picture")){
                                                val painter: Painter = rememberAsyncImagePainter(viewModel.retrieveSavedImageUriByFileName(photo.url))
                                                Image(painter = painter, contentDescription = "event image"
                                                    ,modifier = Modifier.fillMaxSize(), contentScale = ContentScale.FillBounds)
                                            }
                                            else {
                                                Image(
                                                    painter = rememberAsyncImagePainter(photo.url),
                                                    contentDescription = "Event Photo",
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.FillBounds,

                                                    )


                                            }




                                        }
                                        Spacer(modifier = Modifier.width(12.dp))


                                    }


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
                                .height(70.dp)
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
                            Spacer(modifier = Modifier.width(17.dp))
                            val time = viewModel.fromDateTime.toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH:mm"))
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
                            Spacer(modifier = Modifier.width(37.dp))
                            val toTime = viewModel.toDateTime.toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH:mm"))
                            Text(
                                modifier = Modifier.clickable { },
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
                                modifier = Modifier.clickable {  },
                                text = toDate,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 15.sp,
                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                    fontWeight = FontWeight(400),
                                    color = Color(0xFF16161C),
                                )
                            )
                            Spacer(modifier = Modifier.width(32.dp))

                        }
                        Spacer(
                            modifier = Modifier
                                .padding(horizontal = 17.dp)
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(color = Color(0xFFEEF6FF))
                        )
                        Row(modifier = Modifier
                            .height(70.00003.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 17.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start,) {
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

                        }
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



                        }
                        Spacer(modifier = Modifier.height(30.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .padding(horizontal = 17.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(30.dp)
                                    .clickable { viewModel.onEvent(EventEvent.GoingButtonClick(0)) }
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
                                    .clickable { viewModel.onEvent(EventEvent.GoingButtonClick(1)) }
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
                                    .clickable { viewModel.onEvent(EventEvent.GoingButtonClick(2)) }
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
                        viewModel.attendees
                            .sortedByDescending { it.userId == viewModel.hostId }
                            .filter { attendee -> if (viewModel.visitorsButtonSelected != 2) attendee.isGoing else !attendee.isGoing }
                            .forEach {

                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .height(46.dp)
                                    .paint(
                                        painter = painterResource(
                                            id = R.drawable.visitors_box

                                        )
                                    )
                                    .padding(
                                        start = 10.dp,
                                        end = if (it.userId == viewModel.hostId) 17.dp else 15.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Box(
                                            modifier = Modifier
                                                .width(32.dp)
                                                .height(32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.circled_avatar_user),
                                                contentDescription = "avatar circle"
                                            )
                                            Text(
                                                text = viewModel.getNameInitials(it.fullName),
                                                style = TextStyle(
                                                    fontSize = 12.sp,
                                                    lineHeight = 14.4.sp,
                                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                                    fontWeight = FontWeight(600),
                                                    color = Color(0xFFFFFFFF),
                                                )
                                            )

                                        }
                                        Spacer(modifier = Modifier.width(15.dp))
                                        Text(
                                            text = it.fullName,
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                lineHeight = 12.sp,
                                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                                fontWeight = FontWeight(500),
                                                color = Color(0xFF5C5D5A),
                                            )
                                        )
                                    }
                                    if (it.userId == viewModel.hostId) {

                                        Text(
                                            modifier = Modifier.padding(end = 17.dp, start = 63.dp),
                                            text = "creator",
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                lineHeight = 15.sp,
                                                fontFamily = FontFamily(Font(R.font.inter_regular)),
                                                fontWeight = FontWeight(500),
                                                color = Color(0xFFB7C6DE),
                                            )
                                        )
                                    }
                                    else {
                                        Box(modifier = Modifier.padding(end = 15.dp, start = 65.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.trash_can_icon_to_delete_attendees
                                                ), contentDescription = "delete")
                                        }

                                    }




                                }
                                Spacer(modifier = Modifier.height(5.dp))
                            }

                        if (viewModel.visitorsButtonSelected == 0 ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 18.dp)
                            ) {
                                Text(
                                    text = "Not going",
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
                            viewModel.attendees.filter { attendee -> !attendee.isGoing }
                                .forEach {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 17.dp)
                                            .height(46.dp)
                                            .paint(
                                                painter = painterResource(
                                                    id = R.drawable.visitors_box

                                                )
                                            )
                                            .padding(
                                                start = 10.dp,
                                                end = if (it.userId == viewModel.hostId) 17.dp else 15.dp
                                            ),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 10.dp)
                                                    .width(32.dp)
                                                    .height(32.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(id = R.drawable.circled_avatar_user),
                                                    contentDescription = "avatar circle"
                                                )
                                                Text(
                                                    text = viewModel.getNameInitials(it.fullName),
                                                    style = TextStyle(
                                                        fontSize = 12.sp,
                                                        lineHeight = 14.4.sp,
                                                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                                                        fontWeight = FontWeight(600),
                                                        color = Color(0xFFFFFFFF),
                                                    )
                                                )

                                            }
                                            Spacer(modifier = Modifier.width(15.dp))
                                            Text(
                                                text = it.fullName,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    lineHeight = 12.sp,
                                                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                                                    fontWeight = FontWeight(500),
                                                    color = Color(0xFF5C5D5A),
                                                )
                                            )
                                        }


                                        Box(modifier = Modifier
                                            .padding(end = 15.dp, start = 65.dp)

                                        ) {
                                            Image(
                                                painter = painterResource(
                                                    id = R.drawable.trash_can_icon_to_delete_attendees
                                                ), contentDescription = "delete"
                                            )
                                        }




                                    }
                                    Spacer(modifier = Modifier.height(5.dp))


                                }
                        }
                            Spacer(modifier = Modifier.height(45.dp))
                            Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                                Text(
                                    text = "DELETE EVENT",
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

}