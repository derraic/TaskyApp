package com.derra.taskyapp.presentation.event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.exp

@Composable
fun ReminderDropdownReal(expanded: Boolean, onDissmissRequest: () -> Unit, onItemClick: (Int) -> Unit) {
    DropdownMenu(modifier = Modifier.width(224.dp).background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(topStart = 7.dp,topEnd = 7.dp))
        ,expanded = expanded, onDismissRequest = { onDissmissRequest() }) {
        DropdownMenuItem(modifier = Modifier.background(color = Color(0xFFFFFFFF)),onClick = { onItemClick(10) }) {
            Row(
                Modifier
                    .width(224.dp)
                    .clickable { onItemClick(10) }
                    .height(47.dp)
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(topStart = 7.dp,topEnd = 7.dp))
                    .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "10 minutes before")
            }
        }
        DropdownMenuItem(modifier = Modifier.background(color = Color(0xFFFFFFFF)),onClick = { onItemClick(30) }) {
            Row(
                Modifier
                    .width(224.dp)
                    .clickable { onItemClick(30) }
                    .height(47.dp)
                    .background(color = Color(0xFFFFFFFF))
                    .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "30 minutes before")
            }
        }
        DropdownMenuItem(modifier = Modifier.background(color = Color(0xFFFFFFFF)),onClick = { onItemClick(60) }) {
            Row(
                Modifier
                    .width(224.dp)
                    .clickable { onItemClick(60) }
                    .height(47.dp)
                    .background(color = Color(0xFFFFFFFF))
                    .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "1 hour before")
            }
        }
        DropdownMenuItem(modifier = Modifier.background(color = Color(0xFFFFFFFF)),onClick = { onItemClick(360) }) {
            Row(
                Modifier
                    .width(224.dp)
                    .clickable { onItemClick(360) }
                    .height(47.dp)
                    .background(color = Color(0xFFFFFFFF))
                    .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "6 hours before")
            }
        }
        DropdownMenuItem(modifier = Modifier.background(color = Color(0xFFFFFFFF)),onClick = { onItemClick(1440) }) {
            Row(
                Modifier
                    .width(224.dp)
                    .clickable { onItemClick(1440) }
                    .height(47.dp)
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(bottomStart = 7.dp,bottomEnd = 7.dp))
                    .padding(start = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "1 day before")
            }
        }

    }
}
