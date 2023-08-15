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
fun EditDesignOfDialog(viewModel: DayTasksViewModel) {
    Column(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000)
            )
            .width(141.dp)
            .height(166.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 7.dp))


    ) {
       Row(modifier = Modifier
           .height(56.dp)
           .clickable {  viewModel.onEvent(DayTaskEvent.OpenItemClick)}
           .fillMaxWidth()
           .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
           Text(
               text = "Open",
               style = TextStyle(
                   fontSize = 16.sp,
                   fontFamily = FontFamily(Font(R.font.inter_regular)),
                   fontWeight = FontWeight(400),
                   color = Color(0xFF16161C),
               )
           )

       }
       Spacer(modifier = Modifier
           .width(141.dp)
           .height(1.dp)
           .background(color = Color(0xFFEEF6FF)))
       Row(modifier = Modifier
           .height(55.dp)
           .clickable { viewModel.onEvent(DayTaskEvent.EditItemClick) }
           .fillMaxWidth()
           .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically){
           Text(
               text = "Edit",
               style = TextStyle(
                   fontSize = 16.sp,
                   fontFamily = FontFamily(Font(R.font.inter_regular)),
                   fontWeight = FontWeight(400),
                   color = Color(0xFF16161C),
               )
           )

       }
        Spacer(modifier = Modifier
            .width(141.dp)
            .height(1.dp)
            .background(color = Color(0xFFEEF6FF)))
       Row(
           modifier = Modifier
               .height(55.dp)
               .fillMaxWidth()
               .clickable { viewModel.onEvent(DayTaskEvent.DeleteItemClick) }
               .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically)
       {
           Text(
               text = "Delete",
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