package com.example.weatherforecast.alert.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.weatherforecast.alert.work_manager.AlertWorker

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val alertId = intent.getStringExtra("id")

        val data = Data.Builder().putString("id", alertId).build()

        val alertWork = OneTimeWorkRequest.Builder(AlertWorker::class.java)
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(alertWork)
    }

}