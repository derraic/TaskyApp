package com.derra.taskyapp.presentation.agenda

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.derra.taskyapp.R
@Composable
fun TimeArrowIndicator() {
    Box (modifier = Modifier
        .fillMaxWidth()
        .height(10.dp)){
        Image(modifier = Modifier
            .height(10.dp)
            .align(Alignment.CenterStart)
            .width(11.dp),painter = painterResource(id = R.drawable.dot_time_arrow), contentDescription = "black dot")
        Spacer(modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(start = 5.dp)
            .fillMaxWidth()
            .height(2.dp)
            .background(color = Color(0xFF16161C)))

        
        
    }

}