package com.example.app.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.app.R

object NotificationHelper {
    private const val CHANNEL_ID = "budget_channel"
    private const val CHANNEL_NAME = "Budget Alerts"

    // Initialize Notification Channel once
    fun createNotificationChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }
    }

    fun sendBudgetAlert(context: Context, message: String) {
        // Ensure notification channel exists
        createNotificationChannel(context)

        // Get the system notification manager
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Build the notification
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Use your own drawable for the notification icon
            .setContentTitle("Budget Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Send the notification with a unique ID
        manager.notify(1, notification)
    }
}
