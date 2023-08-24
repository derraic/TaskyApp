package com.derra.taskyapp.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun EditTitleScreen(
    onTextChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick:  () -> Unit,
    tempString: String
) {

    val textFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent, //Color.Transparent, // Set the background color to transparent
        focusedIndicatorColor = Color.Transparent, // Set the focused indicator color to transparent
        unfocusedIndicatorColor = Color.Transparent // Set the unfocused indicator color to transparent
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFFFFFFFF))
        .padding(horizontal = 17.dp) ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(68.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween)
        {
            Box(modifier = Modifier
                .width(30.dp)
                .height(30.dp)
                .clickable { onBackClick() }
                , contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = R.drawable.back_button_arrow_left), contentDescription = "go back")


            }
            Text(
                text = "Edit title",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 12.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(600),
                    color = Color(0xFF16161C),
                    textAlign = TextAlign.Right,
                )
            )

            Text(
                modifier = Modifier.clickable { onSaveClick() },
                text = "Save",
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 12.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(600),
                    color = Color(0xFF279F70),
                    textAlign = TextAlign.Right,
                )
            )


        }
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(color = Color(0xFFF2F3F7)))

        TextField(
            value = tempString,
            colors = textFieldColors,
            textStyle = TextStyle(
                fontSize = 26.sp,
                lineHeight = 12.sp,
                fontFamily = FontFamily(Font(R.font.inter_regular)),
                fontWeight = FontWeight(400),
                color = Color(0xFF16161C),
            ),
            onValueChange = {
                onTextChange(it) },

            )



    }


}