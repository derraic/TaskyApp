package com.derra.taskyapp.presentation.authentication

import android.window.SplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.derra.taskyapp.R
import com.derra.taskyapp.util.UiEvent

@Composable
fun SplashScreen(
    onNavigate: (UiEvent.Navigate) -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.onEvent(AuthenticationEvent.OnSplashScreenWait)

        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> onNavigate(event)
                else -> Unit
            }

        }

    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF279F70)), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "T",
            style = TextStyle(
                fontSize = 128.sp,
                lineHeight = 45.sp,
                fontFamily = FontFamily(Font(R.font.lobster)),
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
            )
        )


    }
}