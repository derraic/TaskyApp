package com.derra.taskyapp

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.derra.taskyapp.data.objectsviewmodel.Event
import com.derra.taskyapp.util.Routes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: ReminderNotificationService

    override fun onReceive(p0: Context?, intent: Intent?) {



        val title = intent?.getStringExtra("title") ?: return
        val description = intent.getStringExtra("description")
        val id = intent.getStringExtra("id")
        val type = intent.getIntExtra("type", 0)
        if (id != null) {
            Log.d("THIs", "THis is description: $description")
            Log.d("THIs", "THis is id: $id")

            notificationService.showNotification(title = title, id = id, type = type, description = description ?: "")

        }


    }


}