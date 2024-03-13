package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow

class FakeWeatherRepository : IWeatherRepository {
    override suspend fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocalAllLocation(): Flow<List<FaviourateLocationDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertLocation(location: FaviourateLocationDto) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocation(location: FaviourateLocationDto) {
        TODO("Not yet implemented")
    }

    override suspend fun getFavouriteWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alert: Alert) {
        TODO("Not yet implemented")
    }

    override fun getListOfAlerts(): Flow<List<Alert>> {
        TODO("Not yet implemented")
    }

    override fun getAlertWithId(entryId: String?): Alert {
        TODO("Not yet implemented")
    }
}