package com.derra.taskyapp.data

import com.derra.taskyapp.data.objectsviewmodel.*
import com.derra.taskyapp.data.remote.dto.*
import com.derra.taskyapp.data.room.entity.ReminderEntity
import com.derra.taskyapp.data.room.entity.TaskEntity
import com.derra.taskyapp.util.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response


interface TaskyRepository {

    fun registerUser(registrationRequest: RegistrationDto): Call<Void>
    fun loginUser(loginRequest: LoginDto): Call<LoginResponseDto>
    fun checkAuthentication(token: String): Call<Void>
    fun logout(token: String): Call<Void>
    suspend fun getAgenda(token: String, timeZone: String, time: Long): Flow<Resource<AgendaItems>>
    suspend fun syncAgenda(token: String, userId: String): Unit
    suspend fun fullAgenda(token: String): Unit
    suspend fun deleteEventItem(
        token: String,
        eventId: String
    ): Unit

    suspend fun deleteTaskItem(
        token: String,
        taskId: String
    ): Unit

    suspend fun deleteReminderItem(
        token: String,
        reminderId: String
    ): Unit

    suspend fun getEventItem(
        token: String,
        eventId: String
    ): Flow<Resource<Event>>
    suspend fun getTaskItem(
        token: String,
        taskId: String): Flow<Resource<Task>>
    suspend fun getReminderItem(
        token: String,
        reminderId: String
    ): Flow<Resource<Reminder>>

    suspend fun createEvent(
        token: String,
       eventRequest: EventDto,
        photos: List<MultipartBody.Part>,
       host: String = ""
    )

    suspend fun updateEvent(
        token: String,
        eventRequest: EventUpdateDto,
        photos: List<MultipartBody.Part>,
        host: String = ""
    )


    suspend fun getAttendee(
        token: String,
        email: String
    ): AttendeeIsGoing?

    suspend fun deleteAttendee(
        token: String,
        eventId: String
    )



    suspend fun createTask(
        token: String,
        task: TaskEntity
    )

    suspend fun updateTask(
        token: String,
        task: TaskEntity
    )

    suspend fun createReminder(
        token: String,
        reminder: ReminderEntity
    )

    suspend fun updateReminder(
        token: String,
        reminder: ReminderEntity
    )




}