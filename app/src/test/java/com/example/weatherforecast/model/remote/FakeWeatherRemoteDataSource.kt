package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.dto.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRemoteDataSource : IWeatherRemoteDataSource {
    override fun getCurrentWeather(
        lat: Double,
        lon: Double
    ) = flow {
        emit(WeatherResponse(lon = lon , lat = lat))
    }
}