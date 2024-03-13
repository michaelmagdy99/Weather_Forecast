package com.example.weatherforecast.favourite.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.repository.IWeatherRepository
import com.example.weatherforecast.model.repository.WeatherRepository
import com.example.weatherforecast.utilities.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val iWeatherRepo : IWeatherRepository,
) : ViewModel() {

    private val _locationList = MutableStateFlow<ApiState<List<FaviourateLocationDto>>>(ApiState.Loading<List<FaviourateLocationDto>>())
    val locationList : StateFlow<ApiState<List<FaviourateLocationDto>>> = _locationList

    init {
        getAllProduct()
    }

    private fun getAllProduct() {
        viewModelScope.launch(Dispatchers.IO) {
            iWeatherRepo.getLocalAllLocation()
                .catch { e -> _locationList.value = ApiState.Failed(e) }
                .collect{
                    _locationList.value = ApiState.Success(it)
                }
        }
    }

    fun deleteLocation(location: FaviourateLocationDto) {
        viewModelScope.launch(Dispatchers.IO) {
            iWeatherRepo.deleteLocation(location)
            getAllProduct()
        }
    }

    fun insertLocation(location: FaviourateLocationDto) {
        viewModelScope.launch(Dispatchers.IO) {
            iWeatherRepo.insertLocation(location)
        }
    }
}