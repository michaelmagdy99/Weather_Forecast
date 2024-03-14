package com.example.weatherforecast.alert.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.repository.IWeatherRepository

class AlertViewModelFactory(val iWeatherRepo: IWeatherRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertViewModel::class.java)){
            AlertViewModel(iWeatherRepo) as T
        }else{
            throw IllegalArgumentException()
        }
    }
}