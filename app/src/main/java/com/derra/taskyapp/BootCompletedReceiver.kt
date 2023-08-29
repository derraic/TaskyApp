package com.derra.taskyapp

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.derra.taskyapp.data.NotificationAlarmScheduler
import com.derra.taskyapp.data.TaskyRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: TaskyRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val scheduler = NotificationAlarmScheduler(context)

            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO) {
                    val notifications = repository.getAllNotifications()
                    notifications?.forEach { it ->
                        it.let(scheduler::schedule)
                    }

                }

            }



        }
    }
}






