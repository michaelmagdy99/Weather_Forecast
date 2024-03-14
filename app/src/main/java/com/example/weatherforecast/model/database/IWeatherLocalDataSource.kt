package com.example.weatherforecast.model.database

import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {

   suspend fun insertLocation(location: FaviourateLocationDto)

    suspend fun deleteLocation(location: FaviourateLocationDto)

    fun getAllLocation(): Flow<List<FaviourateLocationDto>>

    suspend fun insertAlert(alert: Alert)
    suspend fun deleteAlert(alert: Alert)
    fun getListOfAlerts(): Flow<List<Alert>>
    fun getAlertWithId(entryId: String): Alert
}