package com.example.weatherforecast.utilities

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.example.weatherforecast.R
import com.github.matteobattilana.weather.PrecipType
import com.github.matteobattilana.weather.WeatherView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatter {
    fun convertUnixToTime(unixSeconds: Long?): String {
        val milliseconds = unixSeconds?.times(1000L)
        val date = Date(milliseconds!!)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(date)
    }

    fun getSunriseAndSunset(sunrise: Long, sunset: Long): Pair<String, String> {
        val sunriseTime = convertUnixToTime(sunrise)
        val sunsetTime = convertUnixToTime(sunset)
        return Pair(sunriseTime, sunsetTime)
    }


    fun getFormattedHour(hour: Long?): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(hour?.times(1000) ?: 0)
        return sdf.format(date)
    }

    fun getSuitableBackground(context: Context,desc: String, weatherView: WeatherView): Drawable? {
        return when (desc){
            "01d"-> {
                weatherView.setWeatherData(PrecipType.CLEAR)
                getDrawable(context, R.drawable.sunny_bg)
            }
            "01n"-> {
                weatherView.setWeatherData(PrecipType.CLEAR)
                getDrawable(context, R.drawable.night_bg)
            }
            "02d" , "03d" ,"04d"->{
                weatherView.setWeatherData(PrecipType.CLEAR)
                getDrawable(context, R.drawable.cloduy_mo)
            }
            "02n", "03n", "04n"->{
                weatherView.setWeatherData(PrecipType.CLEAR)
                getDrawable(context, R.drawable.cloudy_bg)
            }
            "09d","9n","10d","10n"-> {
                weatherView.setWeatherData(PrecipType.RAIN)
                getDrawable(context, R.drawable.rainy_bg)
            }
            "13d","13n"-> {
                weatherView.setWeatherData(PrecipType.SNOW)
                getDrawable(context, R.drawable.snow_bg)
            }
            "11d","11n"->{
                weatherView.setWeatherData(PrecipType.RAIN)
                getDrawable(context, R.drawable.rainy_bg)
            }
            "50d","50n"-> {
                weatherView.setWeatherData(PrecipType.SNOW)
                getDrawable(context, R.drawable.snow_bg)
            }
            else ->{
                getDrawable(context, R.drawable.sunny_bg)
            }
        }
    }


    fun getDate(dt: Int?):String {
        val date= Date(dt?.times(1000L) ?: 0)
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale(SettingsConstants.getLang()))
        return formatter.format(date)
    }

    fun getDay(dt: Long?):String{
        val date= Date(dt?.times(1000L) ?: 0)
        val formatter = SimpleDateFormat("EEEE", Locale(SettingsConstants.getLang()))
        return formatter.format(date)
    }

    fun getWeatherImage(img : String):Int {
        return when(img) {
            "01d"-> R.drawable.sunny
            "01n"-> R.drawable.clear_sky_night
            "02d"-> R.drawable.cloudy_sunny
            "02n"-> R.drawable.cloudmoon
            "03d","03n","04d","04n"-> R.drawable.clouds
            "09d","9n","10d","10n"-> R.drawable.rainy_new
            "13d","13n"-> R.drawable.snow_new
            "11d","11n"-> R.drawable.thunderstorms
            "50d","50n"-> R.drawable.storm
            else -> R.drawable.sunny
        }
    }

}