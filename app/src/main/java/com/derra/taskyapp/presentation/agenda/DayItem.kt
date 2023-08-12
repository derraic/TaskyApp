package com.derra.taskyapp.presentation.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derra.taskyapp.R
import com.derra.taskyapp.ui.theme.Shapes

@Composable
fun DayItem(isSelected: Boolean, weekDayInitial: String, dayInitial: String) {
    Column(
        modifier = Modifier
            .width(40.dp)
            .height(61.dp)
            .background(
                color = if (isSelected) Color(0xFFFDEFA8) else Color.Transparent,
                shape = if (isSelected) RoundedCornerShape(100.dp) else Shapes.small
            ),
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = weekDayInitial, modifier = Modifier
                .width(40.dp)
                .height(11.66177.dp), style = TextStyle(
                fontSize = 11.sp,
                lineHeight = 13.2.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(700),
                color = Color(0xFF5C5D5A),
                textAlign = TextAlign.Center,
            )
        )
        Text(
            text = dayInitial, modifier = Modifier
                .width(41.dp)
                .height(32.dp),
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 20.4.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(700),
                color = Color(0xFF5C5D5A),
                textAlign = TextAlign.Center,
            )
        )


    }

}