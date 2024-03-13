package com.example.weatherforecast.model.database

import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import kotlinx.coroutines.flow.Flow

class FakeWeatherLocalDataSource : IWeatherLocalDataSource {
    override suspend fun insertLocation(location: FaviourateLocationDto) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLocation(location: FaviourateLocationDto) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllLocation(): Flow<List<FaviourateLocationDto>> {
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

    override fun getAlertWithId(entryId: String): Alert {
        TODO("Not yet implemented")
    }
}