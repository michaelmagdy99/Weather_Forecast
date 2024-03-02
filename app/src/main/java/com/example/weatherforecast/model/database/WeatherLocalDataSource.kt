package com.example.weatherforecast.model.database

import android.content.Context

class WeatherLocalDataSource private constructor(private val dao: WeatherDao): IWeatherLocalDataSource{

    companion object {
        private var instance: WeatherLocalDataSource? = null

        fun getInstance(context: Context?): WeatherLocalDataSource {
            return instance ?: synchronized(this) {
                val database = WeatherDatabase.getInstance(context!!)
                val dao = database.getWeather()
                val tempInstance = WeatherLocalDataSource(dao)
                instance = tempInstance
                tempInstance
            }
        }
    }
}