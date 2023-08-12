package com.derra.taskyapp.presentation.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.derra.taskyapp.R
import com.derra.taskyapp.util.UiEvent

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthenticationViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit,
) {


    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.PopBackStack -> onPopBackStack()
                else -> Unit
            }

        }

    }

    val textFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent, //Color.Transparent, // Set the background color to transparent
        focusedIndicatorColor = Color.Transparent, // Set the focused indicator color to transparent
        unfocusedIndicatorColor = Color.Transparent // Set the unfocused indicator color to transparent
    )
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = Color(0xFF16161C))) {
        Spacer(modifier = Modifier.height(77.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            , horizontalArrangement = Arrangement.Center   ) {
            Text(
                text = "Create your account",
                style = TextStyle(
                    fontSize = 28.sp,
                    lineHeight = 30.sp,
                    fontFamily = FontFamily(Font(R.font.inter_regular)),
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFFFFFF),
                )
            )

        }
        Spacer(modifier = Modifier.height(42.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .height(658.dp)
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 30.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    )
                )
            ,horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(63.dp)
                .padding(horizontal = 16.dp)
                .background(color = Color(0xFFF2F3F7), shape = RoundedCornerShape(size = 10.dp))
                .padding(horizontal = 20.dp)) {
                TextField(
                    modifier = Modifier.align(Alignment.CenterStart),
                    value = viewModel.name,
                    colors = textFieldColors,
                    onValueChange =  {viewModel.onEvent(AuthenticationEvent.OnNameChange(it)) },
                    placeholder = { Text(
                        text = "Name",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 30.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(400),
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
                if (viewModel.nameCheck) {
                    Image(modifier = Modifier.align(Alignment.CenterEnd),painter = painterResource(id = R.drawable.validation_check_icon), contentDescription = "valid email")
                }


            }
            Spacer(modifier = Modifier.height(15.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(63.dp)
                .padding(horizontal = 16.dp)
                .background(color = Color(0xFFF2F3F7), shape = RoundedCornerShape(size = 10.dp))
                .padding(horizontal = 20.dp)) {
                TextField(
                    modifier = Modifier.align(Alignment.CenterStart),
                    value = viewModel.emailAddress,
                    colors = textFieldColors,
                    onValueChange =  {viewModel.onEvent(AuthenticationEvent.OnEmailAddressChange(it)) },
                    placeholder = { Text(
                        text = "Email address",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 30.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(400),
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
                    Image(modifier = Modifier.align(Alignment.CenterEnd),painter = painterResource(id = R.drawable.validation_check_icon), contentDescription = "valid email")
                }


            }
            Spacer(modifier = Modifier.height(15.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(63.dp)
                .padding(horizontal = 16.dp)
                .background(color = Color(0xFFF2F3F7), shape = RoundedCornerShape(size = 10.dp))
                .padding(horizontal = 20.dp)) {
                TextField(
                    modifier = Modifier.align(Alignment.CenterStart),
                    value = viewModel.password,
                    colors = textFieldColors,
                    onValueChange =  {viewModel.onEvent(AuthenticationEvent.OnPasswordChange(it)) },
                    visualTransformation = if (viewModel.passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    placeholder = { Text(
                        text = "Password",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 30.sp,
                            fontFamily = FontFamily(Font(R.font.inter_regular)),
                            fontWeight = FontWeight(400),
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
                if (viewModel.passwordVisibility) {
                    Image(modifier = Modifier.align(Alignment.CenterEnd).clickable { viewModel.onEvent(
                        AuthenticationEvent.OnVisibilityClick
                    ) },painter = painterResource(id = R.drawable.password_visible_icon), contentDescription = "visible password")
                } else {
                    Image(modifier = Modifier.align(Alignment.CenterEnd).clickable { viewModel.onEvent(
                        AuthenticationEvent.OnVisibilityClick
                    ) },painter = painterResource(id = R.drawable.password_invisible_icon), contentDescription = "invisible password")
                }


            }
            Spacer(modifier = Modifier.height(70.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(horizontal = 16.dp)
                    .background(color = Color(0xFF16161C), shape = RoundedCornerShape(size = 38.dp))
                    .clickable { viewModel.onEvent(AuthenticationEvent.OnRegisterButtonClick) }
                   ,
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Text(
                    text = "GET STARTED",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 30.sp,
                        fontFamily = FontFamily(Font(R.font.inter_regular)),
                        fontWeight = FontWeight(700),
                        color = Color(0xFFF6F1FB),
                    )
                )

            }
            Spacer(modifier = Modifier.height(150.dp))
            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.Start)
                    .width(56.dp)
                    .height(56.dp)
                    .background(color = Color(0xFF16161C), shape = RoundedCornerShape(size = 16.dp))
                    .clickable {viewModel.onEvent(AuthenticationEvent.OnBackButtonClick) }
            ) {
                Image(modifier = Modifier.align(Alignment.Center) ,painter = painterResource(id = R.drawable.back_button_icon), contentDescription = "go back")
            }



        }




    }


}