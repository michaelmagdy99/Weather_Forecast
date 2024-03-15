package com.example.weatherforecast.favourite.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.repository.IWeatherRepository
import com.example.weatherforecast.utilities.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val iWeatherRepo : IWeatherRepository,
) : ViewModel() {

    private val _locationList = MutableStateFlow<UiState<List<FaviourateLocationDto>>>(UiState.Loading<List<FaviourateLocationDto>>())
    val locationList : StateFlow<UiState<List<FaviourateLocationDto>>> = _locationList

    init {
        getAllLocation()
    }

    fun getAllLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            iWeatherRepo.getLocalAllLocation()
                .catch { e -> _locationList.value = UiState.Failed(e) }
                .collect{
                    _locationList.value = UiState.Success(it)
                }
        }
    }

    fun deleteLocation(location: FaviourateLocationDto) {
        viewModelScope.launch(Dispatchers.IO) {
            iWeatherRepo.deleteLocation(location)
            getAllLocation()
        }
    }

    fun insertLocation(location: FaviourateLocationDto) {
        viewModelScope.launch(Dispatchers.IO) {
            iWeatherRepo.insertLocation(location)
        }
    }
}