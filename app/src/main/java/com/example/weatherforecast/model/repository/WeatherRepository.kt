package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepository (
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    //private val weatherLocalDataSource: WeatherLocalDataSource
    ) : IWeatherRepository {

    companion object{
        private var instance : WeatherRepository? = null
        fun getInstance(
            weatherRemoteDataSource: WeatherRemoteDataSource,
            //weatherLocalDataSource: WeatherLocalDataSource
        ): WeatherRepository{
            return instance?: synchronized(this){
                val temp = WeatherRepository(weatherRemoteDataSource) //weatherLocalDataSource)
                instance = temp
                temp
            }
        }
    }
    override suspend fun getCurrentWeather(lat: Double,
                                           lon: Double,
                                           lang: String,
                                           units: String): Flow<WeatherResponse> {
        return weatherRemoteDataSource.getCurrentWeather(lat,lon,lang,units)
    }
}