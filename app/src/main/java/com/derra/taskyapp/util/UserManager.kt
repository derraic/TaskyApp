package com.derra.taskyapp.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
import com.google.gson.Gson


class UserManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "UserPreferences",
        Context.MODE_PRIVATE
    )
    fun saveLoginResponse(loginResponse: LoginResponseDto) {
        val json = Gson().toJson(loginResponse)
        sharedPreferences.edit {
            putString("loginResponse", json)
        }
    }

    fun getLoginResponse(): LoginResponseDto? {
        val json = sharedPreferences.getString("loginResponse", null)
        return Gson().fromJson(json, LoginResponseDto::class.java)
    }
}