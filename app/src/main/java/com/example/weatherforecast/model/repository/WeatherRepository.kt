package com.example.weatherforecast.model.repository

import android.content.Context
import android.util.Log
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.utilities.SettingsConstants
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WeatherRepository (
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val weatherLocalDataSource: WeatherLocalDataSource,
    private val context: Context
    ) : IWeatherRepository {

    companion object{
        private var instance : WeatherRepository? = null
        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            weatherLocalDataSource: WeatherLocalDataSource,
            context: Context
        ): WeatherRepository{
            return instance?: synchronized(this){
                val temp = WeatherRepository(weatherRemoteDataSource, weatherLocalDataSource, context)
                instance = temp
                temp
            }
        }
    }
    override suspend fun getCurrentWeather(): Flow<WeatherResponse> {
        val lat = SharedPreferencesHelper.getInstance(context).loadCurrentLocation("lat")?.toDouble() ?: 29.3059751
        val long = SharedPreferencesHelper.getInstance(context).loadCurrentLocation("long")?.toDouble() ?: 30.8549351
        val lang = SettingsConstants.getLang()
        val unit = "metric"
        Log.i("TAG", "getCurrentWeather: " + lat + long)
        return weatherRemoteDataSource.getCurrentWeather(lat, long, lang,unit)
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

    override suspend fun updateAlertItemLatLongById(id: String, lat: Double, long: Double) {
        withContext(Dispatchers.IO) {
            weatherLocalDataSource.updateAlertItemLatLongById(id, lat, long)
        }
    }

    override fun getAlertWithId(entryId: String): Alert{
        return weatherLocalDataSource.getAlertWithId(entryId)
    }

}