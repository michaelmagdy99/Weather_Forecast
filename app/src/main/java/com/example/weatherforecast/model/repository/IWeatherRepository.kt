package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRepository {
    suspend fun getCurrentWeather(): Flow<WeatherResponse>
    suspend fun getLocalAllLocation() : Flow<List<FaviourateLocationDto>>
    suspend fun insertLocation(location: FaviourateLocationDto)
    suspend fun deleteLocation(location: FaviourateLocationDto)

    suspend fun getFavouriteWeather(lat: Double,
                                  lon: Double,
                                  lang: String,
                                  units: String): Flow<WeatherResponse>


    suspend fun insertAlert(alert: Alert)
    suspend fun deleteAlert(alert: Alert)
    fun getListOfAlerts(): Flow<List<Alert>>

    fun getAlertWithId(entryId: String): Alert

     suspend fun updateAlertItemLatLongById(id: String, lat: Double, long: Double)

}