package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse>
    fun getLocalAllLocation() : Flow<List<FaviourateLocationDto>>
    suspend fun insertLocation(location: FaviourateLocationDto)
    suspend fun deleteLocation(location: FaviourateLocationDto)

     fun getFavouriteWeather(lat: Double, lon: Double): Flow<WeatherResponse>


    suspend fun insertAlert(alert: Alert)
    suspend fun deleteAlert(alert: Alert)
    fun getListOfAlerts(): Flow<List<Alert>>

    fun getAlertWithId(entryId: String?): Alert

}