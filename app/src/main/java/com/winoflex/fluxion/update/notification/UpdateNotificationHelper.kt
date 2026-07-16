package com.winoflex.fluxion.update.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.winoflex.fluxion.R

/**
 * Helper class to manage notifications for update progress.
 */
class UpdateNotificationHelper(private val context: Context) {

    companion object {
        const val UPDATE_CHANNEL_ID = "fluxion_updates"
        const val UPDATE_CHANNEL_NAME = "Fluxion Updates"
        const val UPDATE_NOTIFICATION_ID = 1001
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /**
     * Creates the notification channel if it doesn't already exist.
     */
    fun createUpdateChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                UPDATE_CHANNEL_ID,
                UPDATE_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW // Use low importance for progress
            ).apply {
                description = "Shows progress of app updates"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows or updates a progress notification.
     *
     * @param progress The current download progress (0 to 100).
     * @param fileName The name of the file being downloaded.
     */
    fun showProgressNotification(progress: Int, fileName: String) {
        val notification = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_update) // TODO: Ensure icon exists or use fallback
            .setContentTitle("Downloading Update")
            .setContentText("Downloading $fileName")
            .setOngoing(true)
            .setProgress(100, progress, progress == -1) // -1 for indeterminate
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(UPDATE_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Log if POST_NOTIFICATIONS permission is not granted
            e.printStackTrace()
        }
    }

    /**
     * Clears the update progress notification.
     */
    fun clearNotification() {
        NotificationManagerCompat.from(context).cancel(UPDATE_NOTIFICATION_ID)
    }

    /**
     * Shows a notification when the download is complete.
     */
    fun showCompleteNotification(fileName: String) {
        val notification = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_update)
            .setContentTitle("Download Complete")
            .setContentText("$fileName is ready to install")
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(UPDATE_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Shows an error notification.
     */
    fun showErrorNotification(message: String) {
        val notification = NotificationCompat.Builder(context, UPDATE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_error) // TODO: Ensure icon exists
            .setContentTitle("Update Failed")
            .setContentText(message)
            .setOngoing(false)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(UPDATE_NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
