package com.vishwajeet.securewebbrowserapplication.ui.viewmodel


import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.vishwajeet.securewebbrowserapplication.data.model.HomeUiState
import com.vishwajeet.securewebbrowserapplication.data.model.UrlValidationState

class HomeViewModel : ViewModel() {

    var homeUiState by mutableStateOf(HomeUiState())
        private set


    fun onUrlInputStringChanged(newInput: String) {
        homeUiState = homeUiState.copy(
            urlInput = newInput,
            validationState = UrlValidationState.Idle
        )
    }

    fun validateAndProcessUrlRoute(onValidRouteCallback: (String) -> Unit) {
        val rawInput = homeUiState.urlInput.trim()

        if (rawInput.isBlank()) {
            homeUiState = homeUiState.copy(
                validationState = UrlValidationState.ValidationError("URL field cannot be strictly empty.")
            )
            return
        }

        var polishedUrl = rawInput
        if (!polishedUrl.startsWith("http://") && !polishedUrl.startsWith("https://")) {
            polishedUrl = "https://$polishedUrl"
        }

        if (Patterns.WEB_URL.matcher(polishedUrl).matches()) {
            homeUiState = homeUiState.copy(
                validationState = UrlValidationState.NavigateToWeb(polishedUrl)
            )
            onValidRouteCallback(polishedUrl)
        } else {
            homeUiState = homeUiState.copy(
                validationState = UrlValidationState.ValidationError("Invalid domain infrastructure protocol configuration mapping.")
            )
        }
    }


    fun clearUrlInputFieldContent() {
        homeUiState = homeUiState.copy(
            urlInput = "",
            validationState = UrlValidationState.Idle
        )
    }
}