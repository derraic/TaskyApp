package com.derra.taskyapp.presentation.authentication

sealed class AuthenticationEvent {
    data class OnEmailAddressChange(val emailAddress: String): AuthenticationEvent()
    data class OnPasswordChange(val password: String): AuthenticationEvent()
    data class OnNameChange(val name: String): AuthenticationEvent()
    object OnVisibilityClick: AuthenticationEvent()
    object OnLoginButtonClick: AuthenticationEvent()
    object OnRegisterButtonClick: AuthenticationEvent()
    object OnSignUpClick: AuthenticationEvent()
    object OnSplashScreenWait: AuthenticationEvent()
    object OnBackButtonClick: AuthenticationEvent()
}
