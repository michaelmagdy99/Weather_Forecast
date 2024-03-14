package com.example.weatherforecast.utilities

sealed class ApiState<T> {
    class Success<T>(val data: T) : ApiState<T>()
    class Failed<T>(val msg: Throwable) : ApiState<T>()
    class Loading<T> : ApiState<T>()
}