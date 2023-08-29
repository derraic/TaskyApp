package com.derra.taskyapp

import com.derra.taskyapp.data.room.entity.NotificationEntity

interface AlarmScheduler {
    fun schedule(item: NotificationEntity)
    fun cancel(item: NotificationEntity)
}