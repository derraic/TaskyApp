package com.derra.taskyapp.presentation.event


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.derra.taskyapp.R
import com.derra.taskyapp.presentation.authentication.AuthenticationEvent

@Composable
fun AddAttendeeModal(

    viewModel: EventViewModel

) {
    val textFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent, //Color.Transparent, // Set the background color to transparent
        focusedIndicatorColor = Color.Transparent, // Set the focused indicator color to transparent
        unfocusedIndicatorColor = Color.Transparent // Set the unfocused indicator color to transparent
    )
    Column(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000)
            )
            .width(342.dp)
            .height(277.dp)
            .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 10.dp))
            .padding(top = 15.dp)
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)) {
            Spacer(modifier = Modifier.width(303.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
                    .clickable { viewModel.onEvent(EventEvent.AddAttendeeModalDissmiss) }, contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cross_attendee_modal_exit),
                    contentDescription = "exit"
                )
            }
        }
        Spacer(modifier = Modifier.height(1.dp))


        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Add visitor",
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(700),
                    color = Color(0xFF16161C),
                )
            )
            Spacer(modifier = Modifier.height(30.dp))

            Box(modifier = Modifier
                .width(302.dp)
                .height(63.dp)
                .background(color = Color(0xFFF2F3F7), shape = RoundedCornerShape(size = 10.dp))
                ) {
                TextField(
                    modifier = Modifier.align(Alignment.CenterStart),
                    value = viewModel.emailAddress,
                    colors = textFieldColors,
                    onValueChange =  {viewModel.onEvent(EventEvent.OnEmailAddressChange(it)) },
                    placeholder = { androidx.compose.material.Text(
                        text = "Email address",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 30.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(300),
                            color = Color(0xFFA1A4B2),
                            letterSpacing = 0.8.sp,
                        )
                    )
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 30.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF5C5D5A),
                        letterSpacing = 0.8.sp,
                    )
                )
                if (viewModel.emailAddressCheck) {
                    Image(modifier = Modifier.align(Alignment.CenterEnd).padding(end = 27.dp),painter = painterResource(id = R.drawable.validation_check_icon), contentDescription = "valid email")
                }



            }
            if (viewModel.showErrorText) {
                Text(
                    text = "user with this email doesn't exist",
                    style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFC1B1B),
                ))

            }
            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier
                .width(302.dp)
                .height(50.dp)
                .clickable { viewModel.onEvent(EventEvent.AddAttendeeModalButtonCLick) }
                .background(color = Color(0xFF16161C), shape = RoundedCornerShape(size = 38.dp)),
                contentAlignment = Alignment.Center
                )
            {
                Text(
                    text = "Add",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 30.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFFF2F3F7),
                    )
                )
            }



        }



    }



}