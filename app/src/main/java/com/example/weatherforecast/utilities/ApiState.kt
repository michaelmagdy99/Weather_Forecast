package com.example.weatherforecast.utilities

import com.example.weatherforecast.model.dto.WeatherResponse

sealed class ApiState {
    class Success(val weatherResponse: WeatherResponse) : ApiState()
    class Failed(val msg : Throwable): ApiState()
    object Loading:ApiState()
}