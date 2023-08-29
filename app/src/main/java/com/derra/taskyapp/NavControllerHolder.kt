package com.derra.taskyapp

import android.annotation.SuppressLint
import androidx.navigation.NavController

object NavControllerHolder {
    @SuppressLint("StaticFieldLeak")
    private var navController: NavController? = null

    fun setNavController(controller: NavController) {
        navController = controller
    }

    fun getNavController(): NavController? {
        return navController
    }
}