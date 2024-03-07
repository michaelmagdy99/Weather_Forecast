package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getCurrentWeather(lat: Double,
                                  lon: Double,
                                  lang: String,
                                  units: String): Flow<WeatherResponse>


    suspend fun getLocalAllLocation() : Flow<List<FaviourateLocationDto>>
    suspend fun insertProduct(location: FaviourateLocationDto)
    suspend fun deleteProduct(location: FaviourateLocationDto)
}