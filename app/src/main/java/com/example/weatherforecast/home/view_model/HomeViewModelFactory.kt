package com.example.weatherforecast.home.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.repository.WeatherRepository

class HomeViewModelFactory(private val iWeatherRepo : WeatherRepository,val context: Context)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)){
            HomeViewModel(iWeatherRepo, context) as T
        }else{
            throw IllegalArgumentException("Class Not Found")
        }
    }
}