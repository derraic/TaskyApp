package com.derra.taskyapp.presentation.event

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derra.taskyapp.data.NotificationAlarmScheduler
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.mappers.toReminderEntity
import com.derra.taskyapp.data.mappers_dto_to_entity.toTimestampInDeviceTimeZone
import com.derra.taskyapp.data.objectsviewmodel.Attendee
import com.derra.taskyapp.data.objectsviewmodel.Event
import com.derra.taskyapp.data.objectsviewmodel.Photo
import com.derra.taskyapp.data.objectsviewmodel.Reminder
import com.derra.taskyapp.data.remote.dto.AttendeeDto
import com.derra.taskyapp.data.remote.dto.EventDto
import com.derra.taskyapp.data.remote.dto.EventUpdateDto
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
import com.derra.taskyapp.data.room.entity.NotificationEntity
import com.derra.taskyapp.presentation.authentication.AuthenticationEvent
import com.derra.taskyapp.util.Resource
import com.derra.taskyapp.util.UiEvent
import com.derra.taskyapp.util.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val repository: TaskyRepository,
    private val userManager: UserManager,
    private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var token: String = ""
    var eventItem by mutableStateOf<Event?>(null)
        private set
    var title by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var fromDateTime by mutableStateOf<LocalDateTime>(LocalDateTime.now())
        private set
    var fromDate by mutableStateOf<LocalDate>(LocalDate.now())
        private set
    var fromTime by mutableStateOf<LocalTime>(LocalTime.now())
        private set
    var toDateTime by mutableStateOf<LocalDateTime>(LocalDateTime.now())
        private set
    var toDate by mutableStateOf<LocalDate>(LocalDate.now())
        private set
    var toTime by mutableStateOf<LocalTime>(LocalTime.now())
        private set
    var fromClicked by mutableStateOf(false)

    var remindAt by mutableStateOf<LocalDateTime>(LocalDateTime.now().minusMinutes(30))
    var editMode by mutableStateOf(false)
        private set
    var minutesBefore by mutableStateOf(30)
        private set
    var addAttendeeModal by mutableStateOf(false)
        private set

    var editTitleMode by mutableStateOf(false)
        private set
    var showErrorText by mutableStateOf(false)
        private set
    var editDescriptionMode by mutableStateOf(false)
        private set
    var timeDialog by mutableStateOf(false)
        private set
    var dateDialog by mutableStateOf(false)
        private set
    var reminderDropDown by mutableStateOf(false)
        private set
    var tempString by mutableStateOf("")
        private set
    var deleteDialog by mutableStateOf(false)
        private set
    var newEvent by mutableStateOf(true)
        private set
    var isHost by mutableStateOf(true)
        private set
    var hostId by mutableStateOf("")
        private set
    var attendees by mutableStateOf<List<Attendee>>(emptyList())
        private set
    var visitorsButtonSelected by mutableStateOf(0)
    var photos by mutableStateOf<List<Photo>>(emptyList())
    var hostName by mutableStateOf<String?>(null)
    var isGoing by mutableStateOf(true)
    var emailAddressCheck by mutableStateOf(false)
        private set
    var emailAddress by mutableStateOf("")
    var photoScreenOpen by mutableStateOf(false)
        private set
    var photosListFile by mutableStateOf<List<File>>(emptyList())


    var photosDeleted by mutableStateOf<List<String>>(emptyList())
    var imageString by mutableStateOf("none")
        private set
    var photoToOpen by mutableStateOf<Photo?>(null)
    val deletedPhotoIndices = mutableListOf<Int>()

    private var userId = ""



    private val _uiEvent = Channel<UiEvent>()

    val uiEvent = _uiEvent.receiveAsFlow()
    private val outputDir: File? = context.getDir(DIRECTORY_NAME, Context.MODE_PRIVATE)

    init {
        val eventId = savedStateHandle.get<String>("eventId")!!
        val isEditable = savedStateHandle.get<Boolean>("isEditable")!!
        if (!outputDir?.exists()!!) {
            outputDir.mkdirs()
        }
        val userInfo = getLoginResponse()!!
        userId = userInfo.userId
        token = getLoginResponse()!!.token
        token = "Bearer $token"
        editMode = isEditable


        Log.d("TEST", "This is isEditable: $isEditable")

        if (eventId != "NONE") {
            newEvent = false
            isGoing = false



            viewModelScope.launch {
                repository.getEventItem(token,eventId).collectLatest {resource ->
                    when (resource) {
                        is Resource.Success -> {
                            eventItem = resource.data
                            if (eventItem == null) {
                                return@collectLatest
                            }
                            title = eventItem?.title ?: ""
                            description = eventItem?.description ?: ""
                            fromDateTime = eventItem?.from!!

                            remindAt = eventItem?.remindAt!!

                            minutesBefore = calculateReminderTime(fromDateTime, remindAt = remindAt)
                            fromTime = fromDateTime.toLocalTime()!!
                            fromDate = fromDateTime.toLocalDate()
                            toDateTime = eventItem!!.to
                            toDate = toDateTime.toLocalDate()
                            hostId = eventItem!!.host
                            toTime = toDateTime.toLocalTime()
                            isHost = eventItem!!.isUserEventCreator
                            attendees = eventItem!!.attendees
                            isGoing = attendees.filter { it.userId == getLoginResponse()!!.userId }[0].isGoing
                            photos = eventItem!!.photos




                        }
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {

                        }


                    }


                }




            }





        }
        else {
            hostName = getLoginResponse()!!.fullName
            hostId = getLoginResponse()!!.userId

        }



    }
    private var index = 0
    private val scheduler = NotificationAlarmScheduler(context)

    fun onEvent(event: EventEvent) {
        when (event) {
            is EventEvent.EditTitleClick -> {
                editTitleMode = true
                tempString = title

            }
            is EventEvent.OnImageChange -> {
                imageString = if (event.uri == null) {

                    "none"
                } else {
                    Log.d("TESTT", "THIS SHOULD HAPPEN first and ")
                    saveImageToFile(context = context , event.uri)
                }
                if (imageString != "none") {
                    Log.d("TESTT", "THIS SHOULD HAPPEN and this is the imagestring: $imageString")
                    photos = photos.toMutableList().apply {
                        add(Photo("picture$index", imageString))
                    }
                    index++

                    photosListFile = photosListFile.toMutableList().apply {
                        add(retrieveSavedImageUriByFileName(imageString)!!)
                    }
                    //imageUri = retrieveSavedImageUriByFileName(imageString)
                    Log.d("TESTT", "THIS is the image file now: $imageString")
                    Log.d("PHOTO", "THIS IS PHOTOLISTFILE: $photosListFile")
                    Log.d("PHOTO", "THIS IS PHOTOLISTFILEsize: ${photosListFile.size}")

                }

            }
            is EventEvent.AddAttendeeModalButtonCLick -> {
                Log.d("ths", "This is $emailAddressCheck")
                if (emailAddressCheck) {
                    viewModelScope.launch {
                        val response = repository.getAttendee(token, email = emailAddress)
                        Log.d("ths", "This is $response")

                        if (response != null) {
                            Log.d("ths", "This is ${response.doesUserExist}")
                            if (!response.doesUserExist) {
                                Log.d("TEST", "this is what should be activated")
                                showErrorText = true
                            }
                            else {
                                val newAttendee = Attendee(
                                    email =  response.attendeeInfo?.email ?: "",
                                    fullName = response.attendeeInfo?.fullName ?: "",
                                    userId = response.attendeeInfo?.userId ?: "",
                                    isGoing = true,
                                    remindAt = LocalDateTime.now(),
                                    eventId = ""
                                )
                                attendees = attendees.toMutableList().apply {
                                    add(newAttendee)
                                }
                                addAttendeeModal = false

                            }



                        }
                    }

                }

            }
            is EventEvent.AddAttendeeModalDissmiss -> {
                addAttendeeModal = false

            }
            is EventEvent.AddAttendeeModalOpen -> {
                addAttendeeModal = true
            }
            is EventEvent.OnEmailAddressChange -> {
                emailAddressCheck = isEmailValid(event.emailAddress)
                showErrorText = false
                emailAddress = event.emailAddress
            }
            is EventEvent.OnTextChange -> {
                tempString = event.text
            }
            is EventEvent.DifferentReminderTimeClick -> {
                reminderDropDown = false
                remindAt = fromDateTime.minusMinutes(event.minutes.toLong())
                minutesBefore = event.minutes
            }
            is EventEvent.ReminderTimeDismiss -> {
                reminderDropDown = false

            }
            is EventEvent.DeleteEventClick -> {
                deleteDialog = true


            }
            is EventEvent.EditFromDateClick -> {
                fromClicked = true
                dateDialog = true

            }
            is EventEvent.EditToDateClick -> {
                fromClicked = false
                dateDialog = true

            }
            is EventEvent.AdjustNotificationClick -> {
                reminderDropDown = true


            }
            is EventEvent.EditDescriptionClick -> {
                editDescriptionMode = true
                tempString = description
            }
            is EventEvent.EditFromTimeClick -> {
                timeDialog = true
                fromClicked = true

            }
            is EventEvent.EditToTimeClick -> {
                timeDialog = true
                fromClicked = false

            }

            is EventEvent.OnBackButtonTextFieldClick -> {
                tempString = ""
                editDescriptionMode = false
                editTitleMode = false

            }
            is EventEvent.OnCrossButtonClick -> {
                sendUiEvent(UiEvent.PopBackStack)

            }
            is EventEvent.SaveNewDescriptionClick -> {
                description = tempString
                tempString = ""
                editDescriptionMode = false
            }
            is EventEvent.SaveNewTitleClick -> {
                title = tempString
                tempString = ""
                editTitleMode = false

            }
            is EventEvent.SaveButtonClick -> {

                if (title.isBlank()) {
                    return
                }
                Log.d("PHOTO", "THIS IS PHOTOLISTFILE: $photosListFile")
                Log.d("PHOTO", "THIS IS PHOTOLISTFILE: ${photosListFile.size}")
                photosListFile = photosListFile.toMutableList().apply {
                    // Remove files corresponding to deleted photos using their indices
                    deletedPhotoIndices.forEach { indexToDelete ->
                        removeAt(indexToDelete)
                    }
                }

                val newPhotosListFile = mutableListOf<File>()
                var counter = 0
                for (photoFile in photosListFile) {
                    val compressedFile = compressPhotoIfNeeded(photoFile)
                    if (compressedFile != null) {
                        newPhotosListFile.add(compressedFile)
                    } else {
                        counter++
                    }
                }

                if (counter > 0){
                    Log.d("TEST","this is how many went wrong: $counter")
                    sendUiEvent(UiEvent.ShowToast("$counter ${if (counter == 1) "photo" else "photos"} were skipped because they were too large "))
                }
                Log.d("TEST","this is how many went wrong: $counter")

                photosListFile = newPhotosListFile
                Log.d("PHOTO", "THIS IS PHOTOLISTFILE: $photosListFile")

                if (newEvent) {
                    viewModelScope.launch {
                        val id = UUID.randomUUID().toString()
                        repository.createEvent(token, eventRequest = EventDto(id = id,
                            title = title, description = description, from = fromDateTime.toTimestampInDeviceTimeZone(), to = toDateTime.toTimestampInDeviceTimeZone(), remindAt = remindAt.toTimestampInDeviceTimeZone(), attendeeIds = attendees.map { it.userId }), photos = photosListFile , hostId = getLoginResponse()!!.userId)
                        val notification = NotificationEntity(id = id, name = title, description = description, itemType = 0, remindAt)
                        notification.let(scheduler::schedule)
                        repository.insertNotification(notification)

                    }

                }
                else {
                    viewModelScope.launch {
                        repository.updateEvent(token, eventRequest = EventUpdateDto(id = eventItem!!.id,
                            title = title, description = description, from = fromDateTime.toTimestampInDeviceTimeZone(), to = toDateTime.toTimestampInDeviceTimeZone(), remindAt = remindAt.toTimestampInDeviceTimeZone(), attendeeIds = attendees.map { it.userId }, deletedPhotoKeys = photosDeleted, isGoing = isGoing ), photos = photosListFile , userId = getLoginResponse()!!.userId)
                        val notification = NotificationEntity(id = eventItem!!.id, name = title, description = description, itemType = 0, remindAt)
                        notification.let(scheduler::schedule)
                        repository.insertNotification(notification)
                    }
                }




                editMode = false

            }
            is EventEvent.EditIconClick -> {
                editMode = true
            }

            is EventEvent.OnDateChange -> {

                if (fromClicked) {
                    fromDate = event.date
                    fromDateTime = LocalDateTime.of(fromDate, fromTime)
                }
                else {
                    toDate = event.date
                    toDateTime = LocalDateTime.of(toDate, toTime)

                }
                dateDialog = false
            }
            is EventEvent.OnDateDismiss -> {
                dateDialog = false
            }
            is EventEvent.OnTimeDismiss -> {
                timeDialog = false
            }
            is EventEvent.OnTimeChange -> {
                if (fromClicked)  {
                    fromTime =  event.time
                }
                else {
                    toTime = event.time
                }

            }
            is EventEvent.GoingButtonClick -> {
                visitorsButtonSelected = event.number
            }
            is EventEvent.LeaveOrJoinEventClick -> {
                isGoing = !isGoing
                attendees = attendees.map { attendee ->
                    if (attendee.userId == userId) {
                        attendee.copy(isGoing = isGoing) // Update the isGoing property
                    } else {
                        attendee // Keep the attendee unchanged
                    }
                }.toMutableList()

            }
            is EventEvent.AddPhotoClick -> {
                handleAddImage(event.galleryLauncher)
            }
            is EventEvent.PhotoScreenCloseClick -> {
                photoScreenOpen = false
            }
            is EventEvent.PhotoScreenDeleteClick -> {
                if (photoToOpen!!.key.startsWith("picture")){
                    val deletedIndex = getIndexFromKey(photoToOpen!!.key)
                    deletedPhotoIndices.add(deletedIndex!!)
                }
                else {
                    photosDeleted = photosDeleted.toMutableList().apply { add(photoToOpen!!.key) }
                }

                val index = photos.indexOf(photoToOpen)
                photos = photos.toMutableList().apply {
                    removeAt(index)
                }
                photoScreenOpen = false
            }
            is EventEvent.PhotoScreenOpenClick -> {
                photoToOpen = event.photo
                photoScreenOpen = true
            }
            is EventEvent.DeleteAttendeeClick -> {
                val index = attendees.indexOf(event.attendee)
                attendees = attendees.toMutableList().apply {
                    removeAt(index)
                }

            }
            is EventEvent.OnTimeSave -> {
                if (fromClicked) {
                    fromDateTime = LocalDateTime.of(fromDate, fromTime)
                }
                else {
                    toDateTime = LocalDateTime.of(toDate, toTime)
                }

                timeDialog = false
            }
            is EventEvent.DeleteCancelClick -> {
                deleteDialog = false
            }
            is EventEvent.DeleteConfirmClick -> {
                deleteDialog = false
                if (eventItem != null) {
                    viewModelScope.launch {
                        repository.deleteReminderItem(token, reminderId = eventItem!!.id)
                        val notification = repository.getNotificationById(eventItem!!.id)
                        notification?.let(scheduler::cancel)
                    }

                    sendUiEvent(UiEvent.PopBackStack)
                }
                else {
                    sendUiEvent(UiEvent.PopBackStack)
                }

            }

        }
    }
    fun giveReminderString(minutesBefore: Int): String {
        return when (minutesBefore) {
            10 -> "10 minutes before"
            30 -> "30 minutes before"
            60 -> "1 hour before"
            360 -> "6 hours before"
            1440 -> "day before"
            else -> "$minutesBefore minutes before"
        }
    }

    private fun calculateReminderTime(dateTime: LocalDateTime, remindAt: LocalDateTime): Int {
        val duration = Duration.between(dateTime, remindAt)

        val minutes = duration.toMinutes()

        return when {
            minutes <= 10 -> 10
            minutes <= 30 -> 30
            minutes <= 60 -> 60
            minutes <= 360 -> 360
            minutes <= 1440 -> 1440
            else -> 1440
        }
    }

    private fun getLoginResponse(): LoginResponseDto? {
        return userManager.getLoginResponse()
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }


    private fun isTwoWords(input: String): Boolean {
        val words = input.trim().split("\\s+".toRegex())
        return words.size == 2
    }

    private fun isOneWord(input: String): Boolean {
        val words = input.trim().split("\\s+".toRegex())
        return words.size == 1
    }

    private fun saveImageToFile(context: Context, imageUri: Uri) : String {
        // Generate a unique file name or use a unique identifier for the image

        val fileName = "image_${System.currentTimeMillis()}.jpg"

        // Get the output file directory where you want to save the images


        // Create the output file object
        val outputFile = File(outputDir, fileName)

        // Copy the selected image to the output file
        val inputStream: InputStream? = context.contentResolver?.openInputStream(imageUri)
        inputStream?.let { input ->
            val outputStream: OutputStream = FileOutputStream(outputFile)
            input.copyTo(outputStream, DEFAULT_BUFFER_SIZE)
            outputStream.close()
            input.close()
        }

        // Make a mapping for the pictures or update your data structure to include the file path or other relevant details
        // For example, you can use a list of file paths:
        return fileName

        // Handle the saved image file and update your UI or perform further operations as needed
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(pattern.toRegex())
    }

    fun getNameInitials(name: String) : String{
        return if (isOneWord(name)) {
            name.take(2)
        } else if (isTwoWords(name)) {
            val words = name.trim().split("\\s+".toRegex())
            "${words[0].first()}${words[1].first()}"
        } else {
            val words = name.trim().split("\\s+".toRegex())
            "${words[0].first()}${words.last().first()}"
        }


    }

    private fun handleAddImage(galleryLauncher: ActivityResultLauncher<String>
    ) {
        openGallery(galleryLauncher = galleryLauncher)
    }

    private fun openGallery(galleryLauncher: ActivityResultLauncher<String>) {
        galleryLauncher.launch("image/*")
    }

    fun retrieveSavedImageUriByFileName(fileName: String): File? {
        val files: Array<File>? = outputDir?.listFiles()

        return files?.find { it.name == fileName }
    }

    private fun getIndexFromKey(key: String): Int? {
        if (key.startsWith("picture")) {
            val indexString = key.removePrefix("picture")
            return indexString.toIntOrNull()
        }
        return null
    }

    private fun compressPhotoIfNeeded(photoFile: File): File? {
        if (photoFile.length() > 1024 * 1024) { // 1MB in bytes
            val options = BitmapFactory.Options()
            options.inSampleSize = 4 // Adjust as needed
            val bitmap = BitmapFactory.decodeFile(photoFile.path, options)

            val compressedFile = File.createTempFile("compressed_", ".jpg")
            val outputStream = FileOutputStream(compressedFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) // Adjust quality as needed

            outputStream.flush()
            outputStream.close()

            return if (compressedFile.length() > 1024 * 1024) null else compressedFile
        }
        return photoFile
    }

    companion object {
        const val DIRECTORY_NAME = "UserEventImages"
        const val GALLERY_LAUNCHER_REQUEST_CODE = 123
    }




}