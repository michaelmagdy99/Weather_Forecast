package com.example.weatherforecast.home.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.repository.WeatherRepository

class HomeViewModelFactory(private val iWeatherRepo : WeatherRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(iWeatherRepo) as T
        }else{
            throw IllegalArgumentException("Class Not Found")
        }
    }
}