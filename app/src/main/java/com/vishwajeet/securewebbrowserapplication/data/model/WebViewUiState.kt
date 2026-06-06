package com.vishwajeet.securewebbrowserapplication.data.model

data class WebViewUiState(
    val currentUrl: String = "",
    val webPageTitle: String = "",
    val loadingProgress: Int = 0,
    val isLoadingActive: Boolean = true,
    val networkErrorMessage: String? = null
)