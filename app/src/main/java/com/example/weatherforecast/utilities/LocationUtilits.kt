package com.example.weatherforecast.utilities

import android.content.Context
import android.location.Geocoder
import android.location.Location
import java.io.IOException

object LocationUtils {
    fun getAddress(context: Context, location: Location): String {
        val geocoder = Geocoder(context)
        try {
            val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (list != null && list.isNotEmpty()) {
                return "${list[0].locality}, ${list[0].adminArea}, ${list[0].countryName}"
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Unknown"
    }
}
