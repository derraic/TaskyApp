package com.derra.taskyapp.data.remote.dto

data class LoginResponseDto(
    val fullName: String,
    val token: String,
    val userId: String
)