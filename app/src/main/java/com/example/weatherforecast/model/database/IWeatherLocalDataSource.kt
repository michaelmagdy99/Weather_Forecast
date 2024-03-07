package com.example.weatherforecast.model.database

import com.example.weatherforecast.model.dto.FaviourateLocationDto
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {

   suspend fun insertLocation(location: FaviourateLocationDto)

    suspend fun deleteProduct(location: FaviourateLocationDto)

    suspend fun getAllProducts(): Flow<List<FaviourateLocationDto>>
}