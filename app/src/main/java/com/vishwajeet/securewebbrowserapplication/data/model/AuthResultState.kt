package com.vishwajeet.securewebbrowserapplication.data.model

sealed class AuthResultState {

    object Idle : AuthResultState()
    object Loading : AuthResultState()
    data class Success(val userEmail: String) : AuthResultState()
    data class Error(val message: String) : AuthResultState()

}