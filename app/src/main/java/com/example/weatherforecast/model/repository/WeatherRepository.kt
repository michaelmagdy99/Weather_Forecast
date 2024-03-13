package com.example.weatherforecast.model.repository

import android.util.Log
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.utilities.SettingsConstants
import kotlinx.coroutines.flow.Flow

class WeatherRepository(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource,
    ) : IWeatherRepository {

    companion object{
        private var instance : WeatherRepository? = null
        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            weatherLocalDataSource: WeatherLocalDataSource
        ): WeatherRepository{
            return instance?: synchronized(this){
                val temp = WeatherRepository(weatherRemoteDataSource, weatherLocalDataSource)
                instance = temp
                temp
            }
        }
    }
    override suspend fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse> {
        val lang = SettingsConstants.getLang()
        val unit = "metric"
        Log.i("TAG", "getCurrentWeather: $lat $lon")
        return weatherRemoteDataSource.getCurrentWeather(lat, lon, lang, unit)
    }

    override suspend fun getLocalAllLocation(): Flow<List<FaviourateLocationDto>> {
        return weatherLocalDataSource.getAllLocation()
    }

    override suspend fun insertLocation(location: FaviourateLocationDto) {
        weatherLocalDataSource.insertLocation(location)
    }

    override suspend fun deleteLocation(location: FaviourateLocationDto) {
        weatherLocalDataSource.deleteLocation(location)
    }

    override suspend fun getFavouriteWeather(
        lat: Double,
        lon: Double,
        lang: String,
        units: String
    ): Flow<WeatherResponse> {
        return weatherRemoteDataSource.getCurrentWeather(lat, lon, lang, units)
    }

    override suspend fun insertAlert(alert: Alert) {
        weatherLocalDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert) {
        weatherLocalDataSource.deleteAlert(alert)
    }

    override fun getListOfAlerts(): Flow<List<Alert>> {
        return weatherLocalDataSource.getListOfAlerts()
    }

    override fun getAlertWithId(id: String?): Alert{
        return weatherLocalDataSource.getAlertWithId(id!!)
    }

}