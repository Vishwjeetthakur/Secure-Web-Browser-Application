package com.vishwajeet.securewebbrowserapplication.data.model

data class HomeUiState(
    val urlInput: String = "",
    val validationState: UrlValidationState = UrlValidationState.Idle
)