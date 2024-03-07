package com.example.weatherforecast.favourite.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.home.view_model.HomeViewModel
import com.example.weatherforecast.model.repository.WeatherRepository

class FavouriteViewModelFactory(private val iWeatherRepo : WeatherRepository)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavouriteViewModel::class.java)) {
            FavouriteViewModel(iWeatherRepo) as T
        } else {
            throw IllegalArgumentException("Class Not Found")
        }
    }
}