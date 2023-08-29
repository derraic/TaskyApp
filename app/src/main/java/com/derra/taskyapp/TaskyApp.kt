package com.derra.taskyapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.UserManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.derra.taskyapp.data.TaskyRepository
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class TaskyApp : Application(), Configuration.Provider{

    @Inject lateinit var userManager: com.derra.taskyapp.util.UserManager
    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            "tasky_channel_id",
            "agenda_reminders", NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "Used for sending reminder for agenda items"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)



    }

    @Inject
    lateinit var workerFactory: CustomerWorkerFactory


    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


    class CustomerWorkerFactory@Inject constructor(
        private val repository: TaskyRepository
    ) : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker = SyncWorker(repository = repository,
        appContext =  appContext, workerParams = workerParameters)

    }
}