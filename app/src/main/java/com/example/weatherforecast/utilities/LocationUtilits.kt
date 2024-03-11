package com.example.weatherforecast.utilities

import android.content.Context
import android.location.Geocoder
object LocationUtils {
    fun getAddress(context: Context, lat: Double, lon: Double): String {
        val geocoder = Geocoder(context)
        val list = geocoder.getFromLocation(lat, lon, 1)

        return if (list != null && list.isNotEmpty()) {
            (list[0].countryName + ", " + list[0].adminArea)
        } else {
            "Unknown"
        }
    }
}
