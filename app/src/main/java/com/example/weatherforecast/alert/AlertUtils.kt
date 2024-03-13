package com.example.weatherforecast.alert

import android.app.AlarmManager
import android.app.Notification
import android.app.Notification.CATEGORY_ALARM
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobInfo.PRIORITY_MAX
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat.*
import com.example.weatherforecast.R
import com.example.weatherforecast.alert.reciver.AlarmReceiver

object AlertUtils {
    private const val NOTIFICATION_CHANNEL_ID = "CHANNEL_ID"

    fun sendNotification(context: Context, message:String) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null
        ) {
            val name = context.getString(R.string.app_name)
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(channel)
        }

        val notification = Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle("weather notification")
            .setContentText(message)
            .setCategory(CATEGORY_ALARM)
            .setPriority(PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)
            .setVibrate(longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }

}
