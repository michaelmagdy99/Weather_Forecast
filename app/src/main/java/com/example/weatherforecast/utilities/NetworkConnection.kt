package com.example.weatherforecast.utilities

import android.content.Context
import android.net.ConnectivityManager

object NetworkConnection {
    fun checkConnectionState(activity: Context): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        return nInfo != null && nInfo.isAvailable && nInfo.isConnected
    }
}