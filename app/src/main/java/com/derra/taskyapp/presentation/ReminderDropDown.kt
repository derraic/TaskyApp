package com.derra.taskyapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.unit.dp

@Composable
fun ReminderDropDown(
    onItemClick: (Int) -> Unit

) {
    Column(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000)
            )
            .width(224.dp)
            .height(235.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 7.dp))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick(10) }
                .height(47.dp)
                .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "10 minutes before")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick(30) }
                .height(47.dp)
                .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "30 minutes before")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick(60) }
                .height(47.dp)
                .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "1 hour before")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick(360) }
                .height(47.dp)
                .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "6 hours before")
        }
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { onItemClick(1440) }
                .height(47.dp)
                .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "1 day before")
        }

    }

}