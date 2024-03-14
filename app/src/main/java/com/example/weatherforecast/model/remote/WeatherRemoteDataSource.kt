package com.example.weatherforecast.model.remote

import com.example.weatherforecast.utilities.Constants
import com.example.weatherforecast.utilities.SettingsConstants
import kotlinx.coroutines.flow.flow

class WeatherRemoteDataSource private constructor(private val service: WeatherWebService) : IWeatherRemoteDataSource{

    companion object {
        private var instance: WeatherRemoteDataSource? = null
        fun getInstance(): WeatherRemoteDataSource {
            return instance ?: synchronized(this) {
                val retrofitService = RetrofitHelper.service
                val tempInstance = WeatherRemoteDataSource(retrofitService)
                instance = tempInstance
                tempInstance
            }
        }
    }

    override fun getCurrentWeather(
        lat: Double,
        lon: Double
    )= flow {
        val lang = SettingsConstants.getLang()
        val units = "metric"
        emit(service.getCurrentWeather(lat,lon,Constants.API_KEY,lang,units))
    }


}