package com.example.weatherforecast.model.database

import android.content.Context
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource private constructor(private val dao: WeatherDao): IWeatherLocalDataSource{

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

    override suspend fun deleteProduct(location: FaviourateLocationDto) {
        dao.delete(location)
    }

    override suspend fun getAllProducts(): Flow<List<FaviourateLocationDto>> {
        return dao.getAllLocation()
    }


}