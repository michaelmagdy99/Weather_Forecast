package com.example.weatherforecast.utilities

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatter {
    fun getCurrentDataAndTime() : String{
        val currentDateTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        return dateFormat.format(currentDateTime.time)
    }


     fun getDayOfWeek(timestamp: Long?): String {
        val date = Date(timestamp!! * 1000)
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val dayOfWeek: String = sdf.format(date)
        return dayOfWeek ?: ""
    }


     fun getFormattedHour(hour: Long?): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(hour?.times(1000) ?: 0)
        return sdf.format(date)
    }
}