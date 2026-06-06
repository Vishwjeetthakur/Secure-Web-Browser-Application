package com.vishwajeet.securewebbrowserapplication.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vishwajeet.securewebbrowserapplication.data.repository.BrowserRepository

class ViewModelFactory(
    private val application: Application,
    private  val repository: BrowserRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // 1. BrowserViewModel mapping pipeline configurations
            modelClass.isAssignableFrom(BrowserViewModel::class.java) -> {
                BrowserViewModel(application,repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class mapping layout: ${modelClass.name}")
        }
    }
}