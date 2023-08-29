package com.derra.taskyapp

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.derra.taskyapp.util.Routes
import javax.inject.Inject

class ReminderNotificationService @Inject constructor (private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(title: String, id: String, type: Int, description: String) {

        /*
        var route = ""
        route = if (type == 0) {
            Routes.EDIT_DETAIL_EVENT_SCREEN +
                    "?eventId=$id" + "&isEditable=true"
        } else if (type == 1) {
            Routes.EDIT_DETAIL_REMINDER_SCREEN +
                    "?reminderId=$id" + "&isEditable=true"
        }else {
            Routes.EDIT_DETAIL_EVENT_SCREEN +
                    "?taskId=$id" + "&isEditable=true"
        }
        Log.d("REMINDER", "THis is route: $route, this is $context ")
        val navController = NavControllerHolder.getNavController()!!
        Log.d("REMINDER", "THis is route: $navController, this is ${navController.graph} ")
        val detailIntent = NavDeepLinkBuilder(context)
            .setGraph(navController.graph)
            .setDestination(route)
            .createPendingIntent()
        */

        val notification = NotificationCompat.Builder(context, "tasky_channel_id")
            .setSmallIcon(R.drawable.bell_icon_in_box)
            .setContentTitle(title)
            .setContentText("${description ?: ""}")
            .build()

        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(id.hashCode(), notification)

    }

}