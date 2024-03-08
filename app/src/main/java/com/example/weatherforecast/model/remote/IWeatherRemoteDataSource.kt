package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRemoteDataSource {
     fun getCurrentWeather(
         lat: Double,
         lon: Double,
         lang:String,
         units:String ) : Flow<WeatherResponse>
}