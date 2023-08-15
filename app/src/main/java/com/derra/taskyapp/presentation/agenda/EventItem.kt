package com.derra.taskyapp.presentation.agenda


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derra.taskyapp.R
import com.derra.taskyapp.data.objectsviewmodel.Event

@Composable
fun EventItem(event: Event, startDateTime: String, endDateTime: String, viewModel: DayTasksViewModel) {



    Box(modifier = Modifier
        .shadow(elevation = 4.dp, spotColor = Color(0x1A000000), ambientColor = Color(0x1A000000))
        .fillMaxWidth()
        .defaultMinSize(minHeight = 100.dp)
        .background(color = Color(0xFF279F70), shape = RoundedCornerShape(size = 20.dp))
    ) {
        Column (modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = 15.dp, top = 17.dp)) {
            Row() {

                Image(painter = painterResource(id = R.drawable.checkbox_agenda_items_notselected), contentDescription = "Task not done")

                Spacer(modifier = Modifier.width(12.dp))
                Column() {
                    Text(
                        text = event.title, style = TextStyle(
                            fontSize = 20.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(700),
                            color = Color(0xFF16161C),
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = event.description ?: "", style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(400),
                            color = Color(0xFF5C5D5A),
                        )
                    )


                }


            }
        }
        Box( modifier = Modifier
            .padding(top = 15.dp, end = 14.dp)
            .pointerInput(true) {
                detectTapGestures(onTap = {
                    viewModel.onEvent(DayTaskEvent.IconEditItemClick(event))

                }
                )


            }, contentAlignment = Alignment.TopEnd, ) {
            Image(painter =  painterResource(id = R.drawable.edit_item_notselected) , contentDescription = "edit item")
        }
        DropdownMenu(expanded = viewModel.editDropDownDialog , onDismissRequest = { viewModel.onEvent(DayTaskEvent.EditItemsDialogDismiss)}) {
            DropdownMenuItem(onClick = { viewModel.onEvent(DayTaskEvent.EditItemsDialogDismiss) }) {
                EditDesignOfDialog(viewModel)
            }

        }
        Box(modifier = Modifier.padding(end = 15.dp, bottom = 12.dp), contentAlignment = Alignment.BottomEnd){
            Text(text =  "$startDateTime - $endDateTime", style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = Color(0xFF5C5D5A),
                textAlign = TextAlign.Right,)
            )

        }





    }

}