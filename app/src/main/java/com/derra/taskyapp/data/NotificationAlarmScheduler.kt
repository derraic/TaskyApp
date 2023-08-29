package com.derra.taskyapp.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.derra.taskyapp.AlarmReceiver
import com.derra.taskyapp.AlarmScheduler
import com.derra.taskyapp.data.room.entity.NotificationEntity
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

class NotificationAlarmScheduler @Inject constructor(
    private val context: Context
): AlarmScheduler {


    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override fun schedule(item: NotificationEntity) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", item.name)
            putExtra("description", item.description)
            putExtra("id", item.id)
            putExtra("type", item.itemType)
        }
        Log.d("DEscription", "This is description ${item.description}")
        Log.d("wtf", "THIS IS REMINDAT ${item.remindAt}")
        if (item.remindAt >= LocalDateTime.now()) {
            Log.d("this", "THIS IS REMINDAT ${item.remindAt}")
            val calendar = Calendar.getInstance().apply {
                timeInMillis = item.remindAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(context, item.id.hashCode(),intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
            )
        }





    }

    override fun cancel(item: NotificationEntity) {

        alarmManager.cancel(
            (
            PendingIntent.getBroadcast(context, item.id.hashCode(),
                Intent(context, AlarmReceiver::class.java)
                ,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE )
        )

        )

    }
}