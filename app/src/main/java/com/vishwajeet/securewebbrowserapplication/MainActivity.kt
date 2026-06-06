package com.vishwajeet.securewebbrowserapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.AuthViewModel
import com.vishwajeet.securewebbrowserapplication.util.AppNavGraph
import com.vishwajeet.securewebbrowserapplication.util.NotificationHelper

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private var isAppActiveInForeground = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createWelcomeNotificationChannel(this)


        lifecycle.addObserver(LifecycleEventObserver { _, lifecycleEvent ->
            when (lifecycleEvent) {
                Lifecycle.Event.ON_START -> {
                    isAppActiveInForeground = true
                }
                Lifecycle.Event.ON_STOP -> {
                    isAppActiveInForeground = false

                    if (authViewModel.isUserLoggedSessionActive()) {
                        NotificationHelper.checkAndExecuteWelcomeNotification(this)
                    }
                }
                else -> {}
            }
        })

        setContent {
            val appNavController = rememberNavController()

            var postNotificationPermissionGranted by remember {
                mutableStateOf(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
                    } else true
                )
            }

            val permissionPromptLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                postNotificationPermissionGranted = isGranted
            }

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !postNotificationPermissionGranted) {
                    permissionPromptLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            AppNavGraph(
                navController = appNavController,
                authViewModel = authViewModel
            )
        }
    }
}