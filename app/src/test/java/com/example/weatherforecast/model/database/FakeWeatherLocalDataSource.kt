package com.example.weatherforecast.model.database

import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherLocalDataSource : IWeatherLocalDataSource  {

    private val faviourateLocation : MutableList<FaviourateLocationDto> = mutableListOf()

    private val alerts = mutableListOf<Alert>()

    override suspend fun insertLocation(location: FaviourateLocationDto) {
       if(!faviourateLocation.contains(location)){
           faviourateLocation.add(location)
       }
    }

    override suspend fun deleteLocation(location: FaviourateLocationDto) {
        faviourateLocation.remove(location)
    }

    override fun getAllLocation() = flow {
        emit(faviourateLocation)
    }

    override suspend fun insertAlert(alert: Alert) {
        alerts.add(alert)
    }

    override suspend fun deleteAlert(alert: Alert) {
        alerts.remove(alert)
    }

    override fun getListOfAlerts()= flow {
         emit(alerts)
    }

    override fun getAlertWithId(entryId: String): Alert {
        return alerts.find { it.id == entryId } ?: throw Exception("Alert not found with ID: $entryId")
    }
}