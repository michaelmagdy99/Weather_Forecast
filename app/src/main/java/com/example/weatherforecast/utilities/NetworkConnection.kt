package com.example.weatherforecast.utilities

import android.content.Context
import android.net.ConnectivityManager

object NetworkConnection {
    fun checkConnectionState(activity: Context): Boolean {
        val conn = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = conn.activeNetworkInfo
        return nInfo != null && nInfo.isAvailable && nInfo.isConnected
    }
}