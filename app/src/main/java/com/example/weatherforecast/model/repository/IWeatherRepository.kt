package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.dto.WeatherResponse

interface IWeatherRepository {
    suspend fun getCurrentWeather(lat: Double,
                                  lon: Double,
                                  lang: String,
                                  units: String):WeatherResponse
}