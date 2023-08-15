package com.derra.taskyapp.data.room



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.objectsviewmodel.Task
import com.derra.taskyapp.data.room.entity.DeleteEntity
import com.derra.taskyapp.data.room.entity.EventEntity
import com.derra.taskyapp.data.room.entity.ReminderEntity
import com.derra.taskyapp.data.room.entity.TaskEntity
import java.time.LocalDate

@Dao
interface TaskyDao {

    @Query("SELECT * FROM tasks WHERE date(time) = :day ORDER BY time ASC")
    suspend fun getDayTasks(day: LocalDate): List<TaskEntity>

    @Query("SELECT * FROM reminders WHERE date(time) = :day ORDER BY time ASC")
    suspend fun getDayReminders(day: LocalDate): List<ReminderEntity>

    @Query("SELECT * FROM events WHERE date(startTime) = :day ORDER BY startTime ASC")
    suspend fun getDayEvents(day: LocalDate): List<EventEntity>

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

    @Query("DELETE FROM deletes")
    suspend fun deleteAllDeletes()

}