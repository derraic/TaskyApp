package com.derra.taskyapp.data

import android.media.session.MediaSession.Token
import com.derra.taskyapp.data.mappers.*
import com.derra.taskyapp.data.mappers_dto_to_entity.*
import com.derra.taskyapp.data.objectsviewmodel.*
import com.derra.taskyapp.data.remote.TaskyApi
import com.derra.taskyapp.data.remote.dto.*
import com.derra.taskyapp.data.room.TaskyDao
import com.derra.taskyapp.data.room.entity.DeleteEntity
import com.derra.taskyapp.data.room.entity.EventEntity
import com.derra.taskyapp.data.room.entity.ReminderEntity
import com.derra.taskyapp.data.room.entity.TaskEntity
import com.derra.taskyapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class TaskyRepositoryImpl @Inject constructor(
    private val taskyApi: TaskyApi,
    private val dao: TaskyDao) : TaskyRepository {
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
            val localTasks = dao.getDayTasks(day = dayWithTime).map { it.toTask() }
            val localReminders = dao.getDayReminders(day = dayWithTime).map { it.toReminder() }
            val localEvents = dao.getDayEvents(day = dayWithTime).map { it.toEvent() }
            val agendaItems = AgendaItems(localEvents,localReminders,localTasks)
            emit(Resource.Success(agendaItems))

            val remoteItems = try {
                val response = taskyApi.getAgenda(token = token, timezone = timeZone, time = time)
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
            remoteItems?.let {
                val events: List<EventEntity> = remoteItems.events.map { it.toEventEntity() }
                val tasks: List<TaskEntity> = remoteItems.tasks.map { it.toTaskEntity() }
                val reminders: List<ReminderEntity> = remoteItems.reminders.map { it.toReminderEntity() }
                events.let { eventEntities ->
                    for (eventEntity in eventEntities) {
                        val event = dao.getEventById(eventEntity.id)
                        if (event == null || !event.needsSync) {
                            dao.insertEvent(eventEntity)
                        }

                    }
                }
                tasks.let { taskEntities ->
                    for (taskEntity in taskEntities) {
                        val task = dao.getTaskById(taskEntity.id)
                        if (task == null || !task.needsSync) {
                            dao.insertTask(taskEntity)
                        }

                    }
                }
                reminders.let { reminderEntities ->
                    for (reminderEntity in reminderEntities) {
                        val reminder = dao.getReminderById(reminderEntity.id)
                        if (reminder == null || !reminder.needsSync) {
                            dao.insertReminder(reminderEntity)
                        }

                    }
                }

                val newLocalTasks = dao.getDayTasks(day = dayWithTime).map { it.toTask() }
                val newLocalReminders = dao.getDayReminders(day = dayWithTime).map { it.toReminder() }
                val newLocalEvents = dao.getDayEvents(day = dayWithTime).map { it.toEvent() }
                val newAgendaItems = AgendaItems(newLocalEvents,newLocalReminders,newLocalTasks)
                emit(Resource.Success(newAgendaItems))

            }

        }

    }



    override suspend fun syncAgenda(token: String, userId: String){
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
            var counter = 0
            if (response.isSuccessful) {
                dao.deleteAllDeletes()
                counter++
            }


            eventsToSync.filter { it.kindOfSync == "UPDATE" }.forEach {event ->
                val eventEnt = taskyApi.updateEvent(token = token, eventRequest = event.toEventUpdateDto(dao, userId), photos = emptyList())
                if (eventEnt.isSuccessful) {
                    if (eventEnt.body() != null) {
                        dao.insertEvent(eventEnt.body()!!.toEventEntity())

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
                        dao.insertEvent(eventEnt.body()!!.toEventEntity())
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
            if (counter == 1 + remindersToSync.size + tasksToSync.size + eventsToSync.size) {
                fullAgenda(token)
            }





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
            events.let { eventEntities ->
                for (eventEntity in eventEntities) {
                    dao.insertEvent(eventEntity)
                }
            }
            tasks.let { taskEntities ->
                for (taskEntity in taskEntities) {
                    dao.insertTask(taskEntity)
                }
            }
            reminders.let { reminderEntities ->
                for (reminderEntity in reminderEntities) {
                    dao.insertReminder(reminderEntity)
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

                dao.insertEvent(remoteEvent.toEventEntity())
                val newEvent = dao.getEventById(eventId = eventId)
                emit(Resource.Success(newEvent!!.toEvent()))

            }

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

                dao.insertTask(remoteTask.toTaskEntity())
                val newTask = dao.getTaskById(taskId = taskId)
                emit(Resource.Success(newTask!!.toTask()))

            }
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

                dao.insertReminder(remoteReminder.toReminderEntity())
                val newTask = dao.getReminderById(reminderId = reminderId)
                emit(Resource.Success(newTask!!.toReminder()))

            }

        }
    }

    override suspend fun createEvent(
        token: String,
        eventRequest: EventDto,
        photos: List<MultipartBody.Part>,
        host: String
    ){
        try {
            val event = taskyApi.createEvent(token = token, eventRequest = eventRequest, photos = photos).body()?.toEventEntity()
            if (event != null) {
                dao.insertEvent(event)
            }
            else {
                dao.insertEvent(eventRequest.toEventEntity(host))
            }
        }catch (e: IOException) {
            dao.insertEvent(eventRequest.toEventEntity(host))

        }
        catch (e: HttpException) {
            dao.insertEvent(eventRequest.toEventEntity(host))
        }


    }

    override suspend fun updateEvent(
        token: String,
        eventRequest: EventUpdateDto,
        photos: List<MultipartBody.Part>,
        userId: String
    ){
        try {
            val event = taskyApi.updateEvent(token = token, eventRequest = eventRequest, photos = photos).body()?.toEventEntity()
            if (event != null) {
                dao.insertEvent(event)
            }
            else {
                eventRequest.toEventEntity(dao, userId)?.let { dao.insertEvent(it) }
            }
        } catch (e: IOException) {
            eventRequest.toEventEntity(dao, userId)?.let { dao.insertEvent(it) }


        }
        catch (e: HttpException) {
            eventRequest.toEventEntity(dao, userId)?.let { dao.insertEvent(it) }

        }

    }


    override suspend fun getAttendee(token: String, email: String): AttendeeIsGoing? {
        val response = taskyApi.getAttendee(token = token, email = email)
        return if (response.isSuccessful) {
            response.body()?.toAttendeeIsGoing()
        } else null
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
            taskyApi.createTask(token = token,task = task.toTaskDto())
            dao.insertTask(task)

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


    private fun convertUtcTimestampToLocalDateTime(utcTimestamp: Long, timeZone: String): LocalDateTime {
        val instant = Instant.ofEpochMilli(utcTimestamp)
        val zoneId = ZoneId.of(timeZone)
        return instant.atZone(ZoneId.of("UTC")).withZoneSameInstant(zoneId).toLocalDateTime()
    }
}