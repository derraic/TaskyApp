package com.derra.taskyapp.data

import android.content.Context
import android.util.Log
import com.derra.taskyapp.data.mappers.*
import com.derra.taskyapp.data.mappers_dto_to_entity.*
import com.derra.taskyapp.data.objectsviewmodel.*
import com.derra.taskyapp.data.remote.TaskyApi
import com.derra.taskyapp.data.remote.dto.*
import com.derra.taskyapp.data.room.TaskyDao
import com.derra.taskyapp.data.room.entity.*
import com.derra.taskyapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class TaskyRepositoryImpl @Inject constructor(
    private val taskyApi: TaskyApi,
    private val dao: TaskyDao,
    private val context: Context
    ) : TaskyRepository {
    override suspend fun getAllNotifications(): List<NotificationEntity>? {
        return dao.getAllNotifications()
    }

    override fun registerUser(registrationRequest: RegistrationDto): Call<Void> {
        return taskyApi.registerUser(TaskyApi.API_KEY,registrationRequest)
    }

    override fun loginUser(loginRequest: LoginDto): Call<LoginResponseDto> {
        return taskyApi.loginUser(TaskyApi.API_KEY,loginRequest)
    }

    override fun checkAuthentication(token: String): Call<Void> {
        return taskyApi.checkAuthentication(TaskyApi.API_KEY, token)
    }

    override fun logout(token: String): Call<Void> {
        return taskyApi.logout(TaskyApi.API_KEY, token)
    }



    override suspend fun getAgenda(token: String, timeZone: String, time: Long): Flow<Resource<AgendaItems>> {
        return flow {
            emit(Resource.Loading(true))
            val dayWithTime = convertUtcTimestampToLocalDateTime(time, timeZone).toLocalDate()
            val startOfDay = dayWithTime.atStartOfDay()
            val startOfNextDay = dayWithTime.plusDays(1).atStartOfDay()

            val localTasks = dao.getDayTasks(startOfDay, startOfNextDay).map { it.toTask() }
            Log.d("this is", "Local tasks size: ${localTasks.size}")
            val localReminders = dao.getDayReminders(startOfDay, startOfNextDay).map { it.toReminder() }
            val localEvents = dao.getDayEvents(startOfDay, startOfNextDay).map { it.toEvent() }
            val agendaItems = AgendaItems(localEvents,localReminders,localTasks)
            emit(Resource.Success(agendaItems))

            val remoteItems = try {
                val response = taskyApi.getAgenda(token = token, timezone = timeZone, time = time)
                if (response.isSuccessful) {
                    Log.d(("KK"), "KK")
                    response.body()
                }else  {
                    null

                }
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server check your internet connection"))
                null
            }
            catch (e: HttpException) {
                emit(Resource.Error(
                    "Oops, something went wrong"
                ))
                null
            }
            Log.d("KK", "KK this is ${remoteItems?.events?.size ?: "null"}")
            remoteItems?.let {
                val events: List<EventEntity> = remoteItems.events.map { it.toEventEntity() }
                val tasks: List<TaskEntity> = remoteItems.tasks.map { it.toTaskEntity() }
                val reminders: List<ReminderEntity> = remoteItems.reminders.map { it.toReminderEntity() }
                val deletedIds = dao.getDeletes().map { it.id }
                events.let { eventEntities ->
                    for (eventEntity in eventEntities) {
                        val event = dao.getEventById(eventEntity.id)
                        if (event == null || !event.needsSync) {
                            if (eventEntity.id !in deletedIds) {
                                dao.insertEvent(eventEntity)
                            }

                        }

                    }
                }
                tasks.let { taskEntities ->
                    for (taskEntity in taskEntities) {
                        val task = dao.getTaskById(taskEntity.id)
                        if (task == null || !task.needsSync) {
                            if (taskEntity.id !in deletedIds) {
                                dao.insertTask(taskEntity)
                            }

                        }

                    }
                }
                reminders.let { reminderEntities ->
                    for (reminderEntity in reminderEntities) {
                        val reminder = dao.getReminderById(reminderEntity.id)
                        if (reminder == null || !reminder.needsSync) {
                            if (reminderEntity.id !in deletedIds) {
                                dao.insertReminder(reminderEntity)
                            }

                        }

                    }
                }

                val newLocalTasks = dao.getDayTasks(startOfDay, startOfNextDay).map { it.toTask() }
                val newLocalReminders = dao.getDayReminders(startOfDay, startOfNextDay).map { it.toReminder() }
                val newLocalEvents = dao.getDayEvents(startOfDay, startOfNextDay).map { it.toEvent() }
                val newAgendaItems = AgendaItems(newLocalEvents,newLocalReminders,newLocalTasks)
                emit(Resource.Success(newAgendaItems))

            }

        }

    }



    override suspend fun syncAgenda(token: String){
        try {
            val eventsToSync = dao.getSyncEvents()
            val tasksToSync = dao.getSyncTasks()
            val remindersToSync = dao.getSyncReminders()

            val deletedItems = dao.getDeletes()

            val response = taskyApi.syncAgenda(
                token = token,
                deletedItems = SyncAgendaDto(deletedEventIds = deletedItems.filter { it.type == "EVENT" }.map { it.id },
                    deletedReminderIds = deletedItems.filter { it.type == "REMINDER" }.map { it.id },
                    deletedTaskIds = deletedItems.filter { it.type == "TASK" }.map { it.id })
            )

            if (response.isSuccessful) {
                dao.deleteAllDeletes()

            }


            eventsToSync.filter { it.kindOfSync == "UPDATE" }.forEach {event ->
                val eventEnt = taskyApi.updateEvent(token = token, eventRequest = event.toEventUpdateDto(dao), photos = emptyList())
                if (eventEnt.isSuccessful) {
                    if (eventEnt.body() != null) {
                        dao.insertEvent(eventEnt.body()!!.toEventEntity().copy(needsSync = false))

                    }

                }
            }
            tasksToSync.filter { it.kindOfSync == "UPDATE" }.forEach { task ->
                val response = taskyApi.updateTask(token = token, task = task.toTaskDto())
                if (response.isSuccessful) {
                    dao.insertTask(task.copy(needsSync = false))

                }


            }
            remindersToSync.filter {  it.kindOfSync == "UPDATE"  }.forEach {reminder ->
                val response = taskyApi.updateReminder(token = token, reminder = reminder.toReminderDto())
                if (response.isSuccessful) {
                    dao.insertReminder(reminder = reminder.copy(needsSync = false))
                }


            }
            eventsToSync.filter { it.kindOfSync == "POST" }.forEach {event ->
                val eventEnt = taskyApi.createEvent(token = token, eventRequest = event.toEventDto(), photos = emptyList())
                if (eventEnt.isSuccessful) {
                    if (eventEnt.body() != null) {
                        dao.insertEvent(eventEnt.body()!!.toEventEntity().copy(needsSync = false))
                    }

                }
            }
            tasksToSync.filter { it.kindOfSync == "POST" }.forEach { task ->

                val response = taskyApi.createTask(token = token, task = task.toTaskDto())
                if (response.isSuccessful) {
                    dao.insertTask(task.copy(needsSync = false))
                }


            }
            remindersToSync.filter {  it.kindOfSync == "POST"  }.forEach {reminder ->
                val response = taskyApi.createReminder(token = token, reminder = reminder.toReminderDto())
                if (response.isSuccessful) {
                    dao.insertReminder(reminder = reminder.copy(needsSync = false))
                }


            }

            fullAgenda(token)






        }
        catch (e: IOException) {

        }
        catch (e: HttpException) {

        }


    }


    override suspend fun fullAgenda(token: String) {

        val remoteItems = try {
            val response = taskyApi.fullAgenda(token = token)
            if (response.isSuccessful) {
                response.body()
            }else  {
                null

            }
        } catch (e: IOException) {
            null
        }
        catch (e: HttpException) {
            null
        }
        remoteItems?.let {

            val events: List<EventEntity> = remoteItems.events.map { it.toEventEntity() }
            val tasks: List<TaskEntity> = remoteItems.tasks.map { it.toTaskEntity() }
            val reminders: List<ReminderEntity> = remoteItems.reminders.map { it.toReminderEntity() }
            val scheduler = NotificationAlarmScheduler(context)
            val deletedIds = dao.getDeletes().map { it.id }
            events.let { eventEntities ->
                for (eventEntity in eventEntities) {
                    val notification = getNotificationById(eventEntity.id)
                    if (notification == null) {
                        val notificationNew = NotificationEntity(id = eventEntity.id, name = eventEntity.title,
                            description = eventEntity.description ?: "", itemType = 0, remindAt = eventEntity.remindAt)
                        notificationNew.let(scheduler::schedule)

                    }
                    val event = dao.getEventById(eventEntity.id)
                    if (event == null || !event.needsSync) {
                        if (eventEntity.id !in deletedIds) {
                            dao.insertEvent(eventEntity)
                        }

                    }
                }
            }
            tasks.let { taskEntities ->
                for (taskEntity in taskEntities) {
                    val notification = getNotificationById(taskEntity.id)
                    if (notification == null) {
                        val notificationNew = NotificationEntity(
                            id = taskEntity.id,
                            name = taskEntity.title,
                            description = taskEntity.description ?: "",
                            itemType = 2,
                            remindAt = taskEntity.remindAt
                        )
                        notificationNew.let(scheduler::schedule)

                    }
                    val task = dao.getTaskById(taskEntity.id)
                    if (task == null || !task.needsSync) {
                        if (taskEntity.id !in deletedIds) {
                            dao.insertTask(taskEntity)
                        }

                    }
                }
            }
            reminders.let { reminderEntities ->
                for (reminderEntity in reminderEntities) {
                    val notification = getNotificationById(reminderEntity.id)
                    if (notification == null) {
                        val notificationNew = NotificationEntity(
                            id = reminderEntity.id,
                            name = reminderEntity.title,
                            description = reminderEntity.description ?: "",
                            itemType = 1,
                            remindAt = reminderEntity.remindAt
                        )
                        notificationNew.let(scheduler::schedule)

                    }
                    val reminder = dao.getReminderById(reminderEntity.id)
                    if (reminder == null || !reminder.needsSync) {
                        if (reminderEntity.id !in deletedIds) {
                            dao.insertReminder(reminderEntity)
                        }

                    }
                }
            }


        }

    }

    override suspend fun deleteEventItem(token: String, eventId: String) {
        dao.deleteEventById(eventId)
        try {
            val response = taskyApi.deleteEventItem(token = token, eventId = eventId )
            if (!response.isSuccessful) {
                dao.insertDeletes(DeleteEntity("EVENT", eventId))

            }
        } catch (e: IOException) {
            dao.insertDeletes(DeleteEntity("EVENT", eventId))

        }
        catch (e: HttpException) {
            dao.insertDeletes(DeleteEntity("EVENT", eventId))

        }


    }

    override suspend fun deleteTaskItem(token: String, taskId: String) {
        dao.deleteTaskById(taskId)
        try {
            val response = taskyApi.deleteTaskItem(token = token, taskId = taskId)
            if (!response.isSuccessful) {
                dao.insertDeletes(DeleteEntity("TASK", taskId))

            }
        }catch (e: IOException) {
            dao.insertDeletes(DeleteEntity("TASK", taskId))

        }
        catch (e: HttpException) {
            dao.insertDeletes(DeleteEntity("TASK", taskId))

        }

    }

    override suspend fun deleteReminderItem(token: String, reminderId: String) {
        dao.deleteReminderById(reminderId = reminderId)
        try {
            val response = taskyApi.deleteReminderItem(token = token, reminderId = reminderId)
            if (!response.isSuccessful) {
                dao.insertDeletes(DeleteEntity("REMINDER", reminderId))
            }
        }catch (e: IOException) {
            dao.insertDeletes(DeleteEntity("REMINDER", reminderId))

        }
        catch (e: HttpException) {
            dao.insertDeletes(DeleteEntity("REMINDER", reminderId))

        }


    }

    override suspend fun getEventItem(token: String, eventId: String): Flow<Resource<Event>> {
        return flow {
            emit(Resource.Loading(true))
            val localEvent = dao.getEventById(eventId)
            if (localEvent != null) {
                emit(Resource.Success(localEvent.toEvent()))
            }

            val remoteEvent = try {
                val response = taskyApi.getEventItem(token = token, eventId = eventId)
                if (response.isSuccessful) {
                    response.body()
                }else  {
                    null

                }
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server check your internet connection"))
                null
            }
            catch (e: HttpException) {
                emit(Resource.Error(
                    "Oops, something went wrong"
                ))
                null
            }
            remoteEvent?.let {

                if (localEvent == null || !localEvent.needsSync) {
                    dao.insertEvent(remoteEvent.toEventEntity())
                }




            }
            emit(Resource.Success(dao.getEventById(eventId = eventId)?.toEvent()))
        }
    }

    override suspend fun getTaskItem(token: String, taskId: String): Flow<Resource<Task>> {
        return flow {
            emit(Resource.Loading(true))
            val localTask = dao.getTaskById(taskId)
            if (localTask != null) {
                emit(Resource.Success(localTask.toTask()))
            }

            val remoteTask = try {
                val response = taskyApi.getTaskItem(token = token, taskId = taskId)
                if (response.isSuccessful) {
                    response.body()
                }else  {
                    null

                }
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server check your internet connection"))
                null
            }
            catch (e: HttpException) {
                emit(Resource.Error(
                    "Oops, something went wrong"
                ))
                null
            }
            remoteTask?.let {

                if (localTask == null || !localTask.needsSync){
                    dao.insertTask(remoteTask.toTaskEntity())
                }




            }
            emit(Resource.Success(dao.getTaskById(taskId = taskId)?.toTask()))
        }
    }


    override suspend fun getReminderItem(
        token: String,
        reminderId: String
    ): Flow<Resource<Reminder>> {
        return flow {
            emit(Resource.Loading(true))
            val localReminder = dao.getReminderById(reminderId)
            if (localReminder != null) {
                emit(Resource.Success(localReminder.toReminder()))
            }

            val remoteReminder = try {
                val response = taskyApi.getReminderItem(token = token, reminderId = reminderId)
                if (response.isSuccessful) {
                    response.body()
                }else  {
                    null

                }
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server check your internet connection"))
                null
            }
            catch (e: HttpException) {
                emit(Resource.Error(
                    "Oops, something went wrong"
                ))
                null
            }
            remoteReminder?.let {

                if (localReminder == null || !localReminder.needsSync) {

                    dao.insertReminder(remoteReminder.toReminderEntity())

                }




            }
            emit(Resource.Success(dao.getReminderById(reminderId = reminderId)?.toReminder()))
        }
    }

    override suspend fun createEvent(
        token: String,
        eventRequest: EventDto,
        photos: List<File>,
        hostId: String
    ){
        try {
            val photoParts: List<MultipartBody.Part> = photos.mapIndexed { index, file ->
                createPartFromFile(file, "photo$index")
            }
            Log.d("sss", "This is $photoParts")
            Log.d("sss", "This is $photos")
            val event = taskyApi.createEvent(token = token, eventRequest = eventRequest, photos = photoParts)
            if (event.isSuccessful) {
                Log.d("this", "this is photos ${event.body()!!.photos}")

                dao.insertEvent(event.body()!!.toEventEntity())
            }
            else {
                Log.d("this", "this is photos ")
                dao.insertEvent(eventRequest.toEventEntity(hostId))
            }
        }catch (e: IOException) {
            dao.insertEvent(eventRequest.toEventEntity(hostId))

        }
        catch (e: HttpException) {
            dao.insertEvent(eventRequest.toEventEntity(hostId))
        }


    }

    override suspend fun insertDeletes(deleteEntity: DeleteEntity) {
        dao.insertDeletes(deleteEntity)
    }

    override suspend fun updateEvent(
        token: String,
        eventRequest: EventUpdateDto,
        photos: List<File>,
        userId: String
    ){
        try {
            val photoParts: List<MultipartBody.Part> = photos.mapIndexed { index, file ->
                createPartFromFile(file, "photo$index")
            }
            val event = taskyApi.updateEvent(token = token, eventRequest = eventRequest, photos = photoParts).body()?.toEventEntity()
            if (event != null) {
                dao.insertEvent(event)
            }
            else {
                val startTime =  convertUtcTimestampToLocalDateTime(eventRequest.from, getDeviceTimeZone().id)
                val endTime =  convertUtcTimestampToLocalDateTime(eventRequest.to, getDeviceTimeZone().id)
                val remindAt = convertUtcTimestampToLocalDateTime(eventRequest.to, getDeviceTimeZone().id)

                dao.insertEvent(dao.getEventById(eventRequest.id)!!.copy(title = eventRequest.title, isGoing = eventRequest.isGoing, needsSync = true, kindOfSync = "UPDATE", description = eventRequest.description, startTime = startTime, to = endTime, remindAt = remindAt))

            }
        } catch (e: IOException) {
            val startTime =  convertUtcTimestampToLocalDateTime(eventRequest.from, getDeviceTimeZone().id)
            val endTime =  convertUtcTimestampToLocalDateTime(eventRequest.to, getDeviceTimeZone().id)
            val remindAt = convertUtcTimestampToLocalDateTime(eventRequest.to, getDeviceTimeZone().id)

            dao.insertEvent(dao.getEventById(eventRequest.id)!!.copy(title = eventRequest.title, isGoing = eventRequest.isGoing,  needsSync = true, kindOfSync = "UPDATE",description = eventRequest.description, startTime = startTime, to = endTime, remindAt = remindAt))



        }
        catch (e: HttpException) {
            val startTime =  convertUtcTimestampToLocalDateTime(eventRequest.from, getDeviceTimeZone().id)
            val endTime =  convertUtcTimestampToLocalDateTime(eventRequest.to, getDeviceTimeZone().id)
            val remindAt = convertUtcTimestampToLocalDateTime(eventRequest.to, getDeviceTimeZone().id)

            dao.insertEvent(dao.getEventById(eventRequest.id)!!.copy(title = eventRequest.title, description = eventRequest.description, startTime = startTime, to = endTime, remindAt = remindAt))


        }

    }

    override suspend fun insertNotification(notificationEntity: NotificationEntity) {
        dao.insertNotification(notificationEntity)
    }

    override suspend fun getNotificationById(notificationId: String): NotificationEntity? {
        return dao.getNotificationById(notificationId)
    }


    override suspend fun getAttendee(token: String, email: String): AttendeeIsGoing? {
        try {
            Log.d("response", "this is token $token")
            val response = taskyApi.getAttendee(token = token, email = email)
            Log.d("response", "this is response $response")
            return if (response.isSuccessful) {
                response.body()?.toAttendeeIsGoing()
            } else null

        }
        catch (e: HttpException) {
            Log.d("Response", "ERRORHTTP")
            return null
        }
        catch (e: IOException) {
            Log.d("Response", "IO ERROR")
            return null
        }




    }

    override suspend fun deleteAttendee(token: String, eventId: String) {
        taskyApi.deleteAttendee(token = token, eventId =  eventId)
    }

    override suspend fun createReminder(token: String, reminder: ReminderEntity) {
        try {
            taskyApi.createReminder(token = token, reminder = reminder.toReminderDto())
            dao.insertReminder(reminder)
        }
        catch (e: HttpException) {
            dao.insertReminder(reminder.copy(needsSync = true, kindOfSync = "POST"))
        }
        catch (e: IOException) {
            dao.insertReminder(reminder.copy(needsSync = true, kindOfSync = "POST"))
        }

    }

    override suspend fun createTask(token: String, task: TaskEntity) {
        try {

            val mask = task.toTaskDto()
            val dayWithTime = convertUtcTimestampToLocalDateTime(mask.time, getDeviceTimeZone().id).toLocalDate()
            Log.d("TEST", "this is day time $dayWithTime")
            Log.d("TEST","and this is direct ${task.time}")
            val response = taskyApi.createTask(token = token,task = task.toTaskDto())
            Log.d("wtf", "THIS IS RESPONSE $response")
            if (response.isSuccessful) {
                dao.insertTask(task)
            }else {
                dao.insertTask(task.copy(needsSync = true, kindOfSync = "POST"))
            }


        }
        catch (e: HttpException) {
            dao.insertTask(task.copy(needsSync = true, kindOfSync = "POST"))
        }
        catch (e: IOException) {
            dao.insertTask(task.copy(needsSync = true, kindOfSync = "POST"))
        }
    }

    override suspend fun updateTask(token: String, task: TaskEntity) {
        try {
            taskyApi.updateTask(token = token,task = task.toTaskDto())
            dao.insertTask(task)

        }
        catch (e: HttpException) {
            dao.insertTask(task.copy(needsSync = true, kindOfSync = "UPDATE"))
        }
        catch (e: IOException) {
            dao.insertTask(task.copy(needsSync = true, kindOfSync = "UPDATE"))
        }
    }


    override suspend fun updateReminder(token: String, reminder: ReminderEntity) {
        try {
            taskyApi.updateReminder(token = token, reminder = reminder.toReminderDto())
            dao.insertReminder(reminder)
        }
        catch (e: HttpException) {
            dao.insertReminder(reminder.copy(needsSync = true, kindOfSync = "UPDATE"))
        }
        catch (e: IOException) {
            dao.insertReminder(reminder.copy(needsSync = true, kindOfSync = "UPDATE"))
        }

    }
    fun convertLongToDateTime(timestamp: Long): LocalDateTime {
        val instant = Instant.ofEpochMilli(timestamp)
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }


    private fun convertUtcTimestampToLocalDateTime(utcTimestamp: Long, timeZone: String): LocalDateTime {
        val instant = Instant.ofEpochMilli(utcTimestamp)
        val zoneId = ZoneId.of(timeZone)
        return instant.atZone(ZoneId.of("UTC")).withZoneSameInstant(zoneId).toLocalDateTime()
    }

    private fun createPartFromFile(file: File, paramName: String): MultipartBody.Part {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, file.name, requestFile)
    }
}