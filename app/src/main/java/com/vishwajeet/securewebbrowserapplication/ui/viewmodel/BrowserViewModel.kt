package com.vishwajeet.securewebbrowserapplication.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vishwajeet.securewebbrowserapplication.data.local.HistoryEntity
import com.vishwajeet.securewebbrowserapplication.data.model.WebViewUiState
import com.vishwajeet.securewebbrowserapplication.data.repository.BrowserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowserViewModel(application: Application
,private val repository: BrowserRepository
) : AndroidViewModel(application) {

    var webViewUiState by mutableStateOf(WebViewUiState())
        private set

    val historyStateFlow: StateFlow<List<HistoryEntity>> = repository.allHistoryFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    var lastOpenedUrlInMemory by mutableStateOf("")

    fun updateWebProgressState(progress: Int) {
        webViewUiState = webViewUiState.copy(
            loadingProgress = progress,
            isLoadingActive = progress < 100
        )
    }

    fun onWebPageMetadataReceived(url: String, title: String) {
        lastOpenedUrlInMemory = url
        webViewUiState = webViewUiState.copy(
            currentUrl = url,
            webPageTitle = title,
            networkErrorMessage = null
        )

        saveUrlToLocalRoomDatabase(url, title)
    }

    fun triggerNetworkErrorState(description: String) {
        webViewUiState = webViewUiState.copy(
            networkErrorMessage = description,
            isLoadingActive = false
        )
    }

    private fun saveUrlToLocalRoomDatabase(url: String, title: String) {
        viewModelScope.launch {
            if (url.isNotBlank() && !url.startsWith("file://")) {
                repository.addUrlToBrowsingHistory(url, title)
            }
        }
    }
    fun purgeCompleteHistoryLogs() {
        viewModelScope.launch {
            repository.clearAllBrowsingHistory()
        }
    }
}