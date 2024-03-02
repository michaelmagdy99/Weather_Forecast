package com.example.weatherforecast.home.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val iWeatherRepo : WeatherRepository) : ViewModel() {

    private val _weatherMutableLiveData = MutableLiveData<WeatherResponse>()

    val weatherLiveData : LiveData<WeatherResponse> = _weatherMutableLiveData

    init {
         getCurrentWeather(33.44,
                              -90.08,
                              "en",
                              "")
    }
    fun getCurrentWeather(lat: Double,
                          lon: Double,
                          lang: String,
                          units: String){

        viewModelScope.launch(Dispatchers.IO) {
           val currentWeather = iWeatherRepo.getCurrentWeather(lat,lon,lang,units)
            withContext(Dispatchers.Main){
                _weatherMutableLiveData.postValue(currentWeather)
            }
        }
    }
}