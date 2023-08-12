package com.derra.taskyapp.presentation.authentication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.derra.taskyapp.data.TaskyRepository
import com.derra.taskyapp.data.remote.dto.LoginDto
import com.derra.taskyapp.data.remote.dto.LoginResponseDto
import com.derra.taskyapp.data.remote.dto.RegistrationDto
import com.derra.taskyapp.util.Routes
import com.derra.taskyapp.util.UiEvent
import com.derra.taskyapp.util.UserManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(private val repository: TaskyRepository, private val userManager: UserManager): ViewModel(){
    var emailAddress by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var name by mutableStateOf("")
        private set
    var nameCheck by mutableStateOf(false)
        private set
    var emailAddressCheck by mutableStateOf(false)
        private set
    var passwordVisibility by mutableStateOf(false)
        private set
    var hasToken by mutableStateOf(false)
        private set
    var passwordCheck by mutableStateOf(false)
        private set



    private val _uiEvent = Channel<UiEvent>()
    private var userInfo: LoginResponseDto? = null

    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        userInfo = getLoginResponse()

    }



    fun onEvent(event: AuthenticationEvent) {
        when (event) {
            is AuthenticationEvent.OnEmailAddressChange -> {
                emailAddressCheck = isEmailValid(event.emailAddress)
                emailAddress = event.emailAddress
            }
            is AuthenticationEvent.OnNameChange -> {
                nameCheck = event.name.length in 4..50
                name = event.name
            }
            is AuthenticationEvent.OnPasswordChange -> {
                password = event.password
                passwordCheck = isPasswordValid(event.password)
            }
            is AuthenticationEvent.OnVisibilityClick -> {
                passwordVisibility = !passwordVisibility
            }
            is AuthenticationEvent.OnLoginButtonClick -> {
                if (emailAddressCheck && passwordCheck) {
                    val loginUser = LoginDto(emailAddress, password)
                    Log.d("Login", "This is emai; $emailAddress nd this is password: $password")
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val response = repository.loginUser(loginUser).execute()
                            if (response.isSuccessful) {
                                Log.d("LOGIN","It workedd!!")
                                val loginResponse = response.body()
                                if (loginResponse != null) {
                                    // Use the obtained values as needed
                                    saveLoginResponse(loginResponse)
                                    userInfo = loginResponse
                                }
                                sendUiEvent(UiEvent.Navigate(Routes.DAY_TASKS_SCREEN))
                            } else {
                                Log.d("LOGIN","It did not  work!! this is the respomns ebody: ${response.body()} or : ${response.errorBody()} or ${response.code()}")
                                sendUiEvent(UiEvent.ShowSnackBar("Password or email incorrect"))
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = try {
                                    // Extract the error message from the error body
                                    val errorJson = JSONObject(errorBody)
                                    errorJson.getString("message")
                                } catch (e: JSONException) {
                                    // Fallback to a default error message if parsing fails
                                    Log.d("LOGINFAILED","Login failed")
                                }
                                Log.d("LOGIN", "Login error: $errorMessage")
                                //sendUiEvent(UiEvent.ShowSnackBar(errorMessage))
                                // Handle login error (e.g., show an error message)
                                // Handle login error (e.g., show an error message)
                            }
                        } catch (e: Exception) {
                            Log.d("LOGIN","It disssssd not  work!! this is the error : $e")
                            // Handle exception or show error message
                        }
                    }
                } else {
                    sendUiEvent(UiEvent.ShowSnackBar("Non-valid password or email"))
                }

            }
            is AuthenticationEvent.OnRegisterButtonClick -> {
                if (passwordCheck && emailAddressCheck && nameCheck) {
                    val registrationDto = RegistrationDto(emailAddress,name,password)
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            val response = repository.registerUser(registrationDto).execute()
                            if (response.isSuccessful) {
                                Log.d("REGISTER", "Registration successful")
                                // Handle successful registration
                                sendUiEvent(UiEvent.PopBackStack)
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = try {
                                    // Extract the error message from the error body
                                    val errorJson = JSONObject(errorBody)
                                    errorJson.getString("message")
                                } catch (e: JSONException) {
                                    // Fallback to a default error message if parsing fails
                                    "Registration failed"
                                }
                                Log.d("REGISTER", "Registration error: $errorMessage")
                                // Handle registration error (e.g., show an error message)
                            }
                        } catch (e: Exception) {
                            Log.d("REGISTER", "Exception occurred: $e")
                            // Handle exception or show error message
                        }
                    }
                }


            }
            is AuthenticationEvent.OnBackButtonClick -> {
                sendUiEvent(UiEvent.PopBackStack)
            }

            is AuthenticationEvent.OnSignUpClick -> {
                sendUiEvent(UiEvent.Navigate(Routes.REGISTER_SCREEN))
            }
            is AuthenticationEvent.OnSplashScreenWait -> {
                Log.d("TEST", "I arrived at the right function this is userInfo: s$userInfo s ")
                if (userInfo == null) {
                    sendUiEvent(UiEvent.Navigate(Routes.LOGIN_SCREEN))
                } else {
                    Log.d("TEST", "I ARRIVED")
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            delay(500L)
                            // Execute the API call synchronously

                            val response = repository.checkAuthentication(token = "Bearer ${userInfo!!.token}").execute()

                            if (response.isSuccessful) {
                                Log.d("AUTHENTICATION", "SUCCESSFUL AUTHENTICATION")
                                // Valid authentication, do something
                                if (response.code() == 200) {
                                    sendUiEvent(UiEvent.Navigate(Routes.DAY_TASKS_SCREEN))
                                } else {
                                    sendUiEvent(UiEvent.Navigate(Routes.LOGIN_SCREEN))
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = try {
                                    // Extract the error message from the error body
                                    val errorJson = errorBody?.let { JSONObject(it) }
                                    errorJson?.getString("message")
                                } catch (e: JSONException) {
                                    // Fallback to a default error message if parsing fails
                                    "Authentication failed"
                                }
                                Log.d("AUTHENTICATION", "Authentication error: $errorMessage, ${response.code()}")
                                // Handle authentication error (e.g., show an error message)
                                sendUiEvent(UiEvent.Navigate(Routes.LOGIN_SCREEN))
                            }
                        } catch (e: Exception) {
                            sendUiEvent(UiEvent.Navigate(Routes.LOGIN_SCREEN))
                            Log.d("AUTHENTICATION", "Exception occurred: $e")
                            // Handle exception or show error message
                        }
                    }
                    Log.d("TEST", "OUT OF THE VIEWMODELSCOPE")
                }
            }
        }

    }

    private fun isPasswordValid(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{9,}\$")
        return regex.matches(password)
    }

    private fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private fun saveLoginResponse(loginResponse: LoginResponseDto) {
        userManager.saveLoginResponse(loginResponse)
    }

    private fun getLoginResponse(): LoginResponseDto? {
        return userManager.getLoginResponse()
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(pattern.toRegex())
    }
}