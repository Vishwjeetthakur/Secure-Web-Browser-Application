package com.vishwajeet.securewebbrowserapplication.data.model


sealed class UrlValidationState {
    object Idle : UrlValidationState()

    data class ValidationError(val message: String) : UrlValidationState()

    data class NavigateToWeb(val validatedUrl: String) : UrlValidationState()
}