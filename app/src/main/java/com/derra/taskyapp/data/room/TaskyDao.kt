package com.derra.taskyapp.data.room



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.objectsviewmodel.Task
import com.derra.taskyapp.data.room.entity.*
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface TaskyDao {

    @Query("SELECT * FROM tasks WHERE time >= :startDateTime AND time < :endDateTime ORDER BY time ASC")
    suspend fun getDayTasks(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<TaskEntity>

    @Query("SELECT * FROM reminders WHERE time >= :startDateTime AND time < :endDateTime ORDER BY time ASC")
    suspend fun getDayReminders(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<ReminderEntity>

    @Query("SELECT * FROM events WHERE startTime >= :startDateTime AND startTime < :endDateTime ORDER BY startTime ASC")
    suspend fun getDayEvents(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(events: EventEntity)

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: String)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: String)

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String) : EventEntity?

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String) : TaskEntity?

    @Query("SELECT * FROM reminders WHERE id = :reminderId")
    suspend fun getReminderById(reminderId: String) : ReminderEntity?

    @Query("SELECT * FROM events WHERE needsSync = 1")
    suspend fun getSyncEvents(): List<EventEntity>

    @Query("SELECT * FROM tasks WHERE needsSync = 1")
    suspend fun getSyncTasks(): List<TaskEntity>

    @Query("SELECT * FROM reminders WHERE needsSync = 1")
    suspend fun getSyncReminders(): List<ReminderEntity>

    @Query("SELECT * FROM deletes")
    suspend fun getDeletes(): List<DeleteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun  insertDeletes(deleteEntity: DeleteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notificationEntity: NotificationEntity)
    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: String): NotificationEntity?

    @Query("SELECT * FROM notifications")
    suspend fun getAllNotifications(): List<NotificationEntity>?

    @Query("DELETE FROM deletes")
    suspend fun deleteAllDeletes()

}