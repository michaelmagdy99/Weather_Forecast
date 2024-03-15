package com.example.weatherforecast.utilities

sealed class UiState<T> {
    class Success<T>(val data: T) : UiState<T>()
    class Failed<T>(val msg: Throwable) : UiState<T>()
    class Loading<T> : UiState<T>()
}