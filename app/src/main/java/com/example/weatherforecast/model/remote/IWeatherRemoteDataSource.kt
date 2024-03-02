package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.dto.WeatherResponse

interface IWeatherRemoteDataSource {
    suspend fun getCurrentWeather(lat: Double,
                                  lon: Double,
                                  lang:String,
                                  units:String ) : WeatherResponse
}