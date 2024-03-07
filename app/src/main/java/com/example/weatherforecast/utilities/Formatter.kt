package com.example.weatherforecast.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Geocoder
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.weatherforecast.R
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherData
import com.github.matteobattilana.weather.WeatherView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatter {
    fun getCurrentDataAndTime(): String {
        val currentDateTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEE, dd MMM|hh:mm a", Locale.getDefault())
        val formattedDateTime = dateFormat.format(currentDateTime.time)
        val formattedPeriod = if (currentDateTime.get(Calendar.AM_PM) == Calendar.AM) {
            "Am"
        } else {
            "Pm"
        }
        return formattedDateTime.replaceFirst(formattedPeriod.toLowerCase(), formattedPeriod)
    }

    fun getCurrentPeriod(): String {
        val currentDateTime = Calendar.getInstance()
        return if (currentDateTime.get(Calendar.AM_PM) == Calendar.AM) {
            "AM"
        } else {
            "PM"
        }
    }

    fun getDayOfWeek(timestamp: Long?): String {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)

        val tomorrow = (today + 1) % calendar.getActualMaximum(Calendar.DAY_OF_YEAR)

        timestamp?.let {
            val date = Date(it * 1000)
            val calendarDate = Calendar.getInstance()
            calendarDate.time = date

            return if (calendarDate.get(Calendar.DAY_OF_YEAR) == today) {
                "Today"
            }
            else if (calendarDate.get(Calendar.DAY_OF_YEAR) == tomorrow) {
                "Tomorrow"
            }
            else {
                val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
                sdf.format(date)
            }
        }

        return ""
    }


    fun getFormattedHour(hour: Long?): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(hour?.times(1000) ?: 0)
        return sdf.format(date)
    }

    fun getSuitableBackground(context: Context, desc: String, hour: String, weatherView: WeatherView): Drawable? {
        return when {
            desc.equals("clear sky") -> {
                weatherView.setWeatherData(PrecipType.CLEAR)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.night_bg)
                } else {
                    getDrawable(context,R.drawable.sunny_bg)
                }
            }
            desc.equals("few clouds") -> {
                weatherView.setWeatherData(PrecipType.CLEAR)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.cloudy_bg)
                } else {
                    getDrawable(context,R.drawable.cloduy_mo)
                }
            }
            desc.equals("scattered clouds") -> {
                weatherView.setWeatherData(PrecipType.CLEAR)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.cloudy_bg)
                } else {
                    getDrawable(context,R.drawable.cloduy_mo)
                }
            }
            desc.equals("broken clouds") -> {
                weatherView.setWeatherData(PrecipType.CLEAR)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.cloudy_bg)
                } else {
                    getDrawable(context,R.drawable.cloduy_mo)
                }
            }
            desc.equals("shower rain") -> {
                weatherView.setWeatherData(PrecipType.RAIN)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.cloudy_bg)
                } else {
                    getDrawable(context,R.drawable.cloduy_mo)
                }
            }
            desc.equals("rain") -> {
                weatherView.setWeatherData(PrecipType.RAIN)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.cloudy_bg)
                } else {
                    getDrawable(context,R.drawable.cloduy_mo)
                }
            }
            desc.equals("thunderstorm") -> {
                weatherView.setWeatherData(PrecipType.RAIN)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.cloudy_bg)
                } else {
                    getDrawable(context,R.drawable.cloduy_mo)
                }
            }
            desc.equals("snow") -> {
                weatherView.setWeatherData(PrecipType.SNOW)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.cloudy_bg)
                } else {
                    getDrawable(context,R.drawable.cloduy_mo)
                }
            }
            desc.equals("mist") -> {
                weatherView.setWeatherData(PrecipType.CLEAR)
                if (hour.equals("PM")) {
                    getDrawable(context,R.drawable.cloudy_bg)
                } else {
                    getDrawable(context,R.drawable.cloduy_mo)
                }
            }
            else -> null
        }
    }

}