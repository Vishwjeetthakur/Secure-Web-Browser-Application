package com.vishwajeet.securewebbrowserapplication.util


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.vishwajeet.securewebbrowserapplication.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NotificationHelper {
    private const val CHANNEL_ID = "secure_browser_welcome_alerts"
    private const val NOTIFICATION_ID = 5005
    private const val PREFS_NAME = "secure_browser_notification_store"
    private const val KEY_LAST_SHOWN_DATE = "last_shown_date_stamp"


    fun createWelcomeNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Welcome Notifications"
            val channelDescription = "Triggers user system alerts upon initialization rules"
            val importanceLevel = NotificationManager.IMPORTANCE_DEFAULT

            val nativeChannel = NotificationChannel(CHANNEL_ID, channelName, importanceLevel).apply {
                description = channelDescription
            }

            val systemServiceManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            systemServiceManager.createNotificationChannel(nativeChannel)
        }
    }


    fun checkAndExecuteWelcomeNotification(context: Context) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val corporateTodayString = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val lastSavedDateString = sharedPreferences.getString(KEY_LAST_SHOWN_DATE, "")

        if (lastSavedDateString == corporateTodayString) {
            return
        }

        val navigationIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flagConfiguration = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val explicitPendingIntent = PendingIntent.getActivity(context, 0, navigationIntent, flagConfiguration)

        val structuralNotificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Welcome Back")
            .setContentText("Thanks for opening the app")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(explicitPendingIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(NOTIFICATION_ID, structuralNotificationBuilder.build())


                sharedPreferences.edit().putString(KEY_LAST_SHOWN_DATE, corporateTodayString).apply()
            }
        }
    }
}