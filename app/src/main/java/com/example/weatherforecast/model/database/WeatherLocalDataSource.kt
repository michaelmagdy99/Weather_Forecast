package com.example.weatherforecast.model.database

import android.content.Context
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherLocalDataSource(private val dao: WeatherDao): IWeatherLocalDataSource{

    companion object {
        private var instance: WeatherLocalDataSource? = null

        fun getInstance(context: Context?): WeatherLocalDataSource {
            return instance ?: synchronized(this) {
                val database = WeatherDatabase.getInstance(context!!)
                val dao = database.getWeather()
                val tempInstance = WeatherLocalDataSource(dao)
                instance = tempInstance
                tempInstance
            }
        }
    }

    override suspend fun insertLocation(location: FaviourateLocationDto) {
        dao.insertLocation(location)
    }

    override suspend fun deleteLocation(location: FaviourateLocationDto) {
        dao.delete(location)
    }

    override suspend fun getAllLocation(): Flow<List<FaviourateLocationDto>> {
        return dao.getAllLocation()
    }


    override suspend fun deleteAlert(alert: Alert) {
        dao.deleteAlert(alert)
    }

    override suspend fun insertAlert(alert: Alert) {
        dao.insertAlert(alert)
    }
    override fun getListOfAlerts(): Flow<List<Alert>>{
        return dao.getListOfAlerts()
    }

    override fun getAlertWithId(entryId: String): Alert {
        return dao.getAlertWithId(entryId)
    }
}