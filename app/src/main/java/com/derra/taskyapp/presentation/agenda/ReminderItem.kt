package com.derra.taskyapp.presentation.agenda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derra.taskyapp.R
import com.derra.taskyapp.data.objectsviewmodel.Reminder

@Composable
fun ReminderItem(reminder: Reminder, dateTime: String, viewModel: DayTasksViewModel, key: Int) {
    val key = key
    Box(modifier = Modifier
        .shadow(elevation = 4.dp, spotColor = Color(0x1A000000), ambientColor = Color(0x1A000000))
        .fillMaxWidth()
        .defaultMinSize(minHeight = 118.dp)
        .background(color = Color(0xFFF2F3F7), shape = RoundedCornerShape(size = 20.dp))
    ) {
        var isContextMenuVisible by rememberSaveable{
            mutableStateOf(false)
        }
        Column (modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = 15.dp, top = 17.dp)) {
            Row(modifier = Modifier.height(26.dp), verticalAlignment = Alignment.CenterVertically) {

                Image(modifier = Modifier.height(20.dp).padding(top=2.dp),painter = painterResource(id = R.drawable.checkbox_agenda_items_notselected), contentDescription = "Task not done")

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = reminder.title, style = TextStyle(
                        fontSize = 20.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF16161C),
                    )
                )





            }
            Spacer(modifier = Modifier.height(15.dp))
            Row() {
                Spacer(modifier = Modifier.width(28.dp))
                Text(
                    text = reminder.description ?: "", style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5C5D5A),
                    )
                )
            }

        }
        Column(modifier = Modifier.align(Alignment.TopEnd)) {
            Box( modifier = Modifier
                .padding(top = 15.dp, end = 14.dp)
                .pointerInput(true) {
                    detectTapGestures(onTap = {
                        viewModel.onEvent(DayTaskEvent.IconEditItemClick(reminder))
                        isContextMenuVisible = true

                    }
                    )


                }, ) {
                Image(painter =  painterResource(id = R.drawable.edit_item_notselected) , contentDescription = "edit item")
            }
            DropdownMenu(expanded = isContextMenuVisible , onDismissRequest = { isContextMenuVisible = false}) {
                DropdownMenuItem(onClick = { viewModel.onEvent(DayTaskEvent.EditItemsDialogDismiss) }) {
                    EditDesignOfDialog(viewModel)
                }

            }
        }

        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(end = 15.dp, bottom = 12.dp)){
            Text(text =  dateTime, style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = Color(0xFF5C5D5A),
                textAlign = TextAlign.Right,)
            )

        }





    }

}