package com.example.weatherforecast.home.view_model

import android.util.Log
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

    private val _weatherMutableStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)

    val weatherStateFlow : StateFlow<ApiState> = _weatherMutableStateFlow


     fun getCurrentWeather(lat: Double,
                                  lon: Double,
                                  lang: String,
                                  units: String){

        viewModelScope.launch(Dispatchers.IO) {
          iWeatherRepo.getCurrentWeather(lat,lon,lang,units)
              .catch { _weatherMutableStateFlow.value = ApiState.Failed(it) }
              .collect{
                  Log.i("TAG", "onViewCreated: "+ it.timezone)
                  _weatherMutableStateFlow.value = ApiState.Success(it)
            }
        }
    }
}