package com.example.weatherforecast.utilities

import android.content.Context
import android.net.ConnectivityManager

object NetworkConnection {
    fun checkConnectionState(activity: Context): Boolean {
        val conn = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = conn.activeNetworkInfo
        return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
    }
}