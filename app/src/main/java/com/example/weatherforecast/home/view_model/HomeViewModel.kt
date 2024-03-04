package com.example.weatherforecast.home.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.utilities.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val iWeatherRepo : WeatherRepository) : ViewModel() {

    private val _weatherMutableLiveData = MutableStateFlow<ApiState>(ApiState.Loading)

    val weatherLiveData : StateFlow<ApiState> = _weatherMutableLiveData

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
          iWeatherRepo.getCurrentWeather(lat,lon,lang,units)
              .catch { _weatherMutableLiveData.value = ApiState.Failed(it) }
              .collect{
                _weatherMutableLiveData.value = ApiState.Success(it)
            }
        }
    }
}