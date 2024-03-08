package com.example.weatherforecast.model.database

import com.example.weatherforecast.model.dto.FaviourateLocationDto
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {

   suspend fun insertLocation(location: FaviourateLocationDto)

    suspend fun deleteLocation(location: FaviourateLocationDto)

    suspend fun getAllLocation(): Flow<List<FaviourateLocationDto>>
}