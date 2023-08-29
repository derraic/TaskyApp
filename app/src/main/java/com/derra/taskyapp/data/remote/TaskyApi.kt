package com.derra.taskyapp.data.remote

import com.derra.taskyapp.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface TaskyApi {
    @POST("/register")
    fun registerUser(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Body registrationRequest: RegistrationDto
    ): Call<Void>

    @POST("/login")
    fun loginUser(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Body loginRequest: LoginDto
    ): Call<LoginResponseDto>

    @GET("/authenticate")
    fun checkAuthentication(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
    ): Call<Void>

    @GET("/logout")
    fun logout(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String
    ): Call<Void>

    @GET("/agenda")
    suspend fun getAgenda(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("timezone") timezone: String,
        @Query("time") time: Long
    ): Response<AgendaDto>

    @POST("/syncAgenda")
    suspend fun syncAgenda(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Body deletedItems: SyncAgendaDto
    ): Response<ResponseBody>

    @GET("/fullAgenda")
    suspend fun fullAgenda(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
    ): Response<AgendaDto>

    @DELETE("/event")
    suspend fun deleteEventItem(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("eventId") eventId: String
    ): Response<ResponseBody>

    @DELETE("/task")
    suspend fun deleteTaskItem(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("taskId") taskId: String
    ): Response<ResponseBody>

    @DELETE("/reminder")
    suspend fun deleteReminderItem(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("reminderId") reminderId: String
    ): Response<ResponseBody>

    @GET("/task")
    suspend fun getTaskItem(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("taskId") taskId: String
    ): Response<TaskDto>

    @GET("/reminder")
    suspend fun getReminderItem(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("reminderId") reminderId: String
    ): Response<ReminderDto>

    @GET("/event")
    suspend fun getEventItem(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("eventId") eventId: String
    ): Response<EventResponseDto>

    @Multipart
    @POST("/event")
    suspend fun createEvent(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Part("create_event_request") eventRequest: EventDto,
        @Part photos: List<MultipartBody.Part>
    ): Response<EventResponseDto>

    @Multipart
    @PUT("/event")
    suspend fun updateEvent(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Part("update_event_request") eventRequest: EventUpdateDto,
        @Part photos: List<MultipartBody.Part>
    ): Response<EventResponseDto>

    @GET("/attendee")
    suspend fun getAttendee(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("email") email: String
    ): Response<AttendeeResponseDto>

    @DELETE("/attendee")
    suspend fun deleteAttendee(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Query("eventId") eventId: String
    )

    @POST("/task")
    suspend fun createTask(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Body task: TaskDto
    ) : Response<ResponseBody>

    @PUT("/task")
    suspend fun updateTask(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Body task: TaskDto
    ) : Response<ResponseBody>

    @POST("/reminder")
    suspend fun createReminder(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Body reminder: ReminderDto
    ) : Response<ResponseBody>

    @PUT("/reminder")
    suspend fun updateReminder(
        @Header("x-api-key") apiKey: Int = API_KEY,
        @Header("Authorization") token: String,
        @Body reminder: ReminderDto
    ) : Response<ResponseBody>

    companion object {
        const val BASE_URL = "https://tasky.pl-coding.com/"
        const val API_KEY = 519551238
    }
}