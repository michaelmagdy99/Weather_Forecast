package com.example.weatherforecast.alert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherforecast.R
import java.util.Date

private const val NOTIFICATION_CHANNEL_ID = "weather_alert_channel"
private const val NOTIFICATION_ID_Alarm = 1001

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.i("TAG" , "Receiver" + Date().toString())
        val alarmType = intent?.getSerializableExtra("alarm_type") as? AlertFragment.AlarmType ?: return
        when (alarmType) {
            AlertFragment.AlarmType.NOTIFICATION -> showNotification(context)
            AlertFragment.AlarmType.SOUND -> playAlarmSound(context)
        }
    }

    private fun showNotification(context: Context?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Weather Alert",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(context!!, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Weather Alert")
            .setContentText("Weather Alert Message Here")
            .setSmallIcon(R.drawable.snowy)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Set notification sound
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        notificationBuilder.setSound(alarmSound)

        notificationManager?.notify(NOTIFICATION_ID_Alarm, notificationBuilder.build())
    }

    private fun playAlarmSound(context: Context?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Weather Alert",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager?.createNotificationChannel(channel)
        }

        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context!!, alarmSound)
        ringtone.play()
    }
}