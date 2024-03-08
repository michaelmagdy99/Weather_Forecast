package com.example.weatherforecast.model.repository

import android.content.Context
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import com.example.weatherforecast.utilities.SharedPreferencesHelper
import kotlinx.coroutines.flow.Flow

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
        val lat = SharedPreferencesHelper.getInstance(context).loadData("latitude")?.toDouble() ?: 0.0
        val lon = SharedPreferencesHelper.getInstance(context).loadData("longitude")?.toDouble() ?: 0.0
        return weatherRemoteDataSource.getCurrentWeather(lat,lon,"en","metric")
    }

    override suspend fun getLocalAllLocation(): Flow<List<FaviourateLocationDto>> {
        return weatherLocalDataSource.getAllLocation()
    }

    override suspend fun insertProduct(location: FaviourateLocationDto) {
        weatherLocalDataSource.insertLocation(location)
    }

    override suspend fun deleteProduct(location: FaviourateLocationDto) {
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
}