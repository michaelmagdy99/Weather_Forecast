package com.example.weatherforecast.alert.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.repository.IWeatherRepository
import com.example.weatherforecast.utilities.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertViewModel(private val iWeatherRepo: IWeatherRepository) : ViewModel() {

    private val _alerts = MutableStateFlow<UiState<List<Alert>>>(UiState.Loading())
    val alerts: StateFlow<UiState<List<Alert>>> = _alerts

    init {
        getAllAlerts()
    }
    fun insertAlert(alert: Alert) {
        viewModelScope.launch {
            iWeatherRepo.insertAlert(alert)
            getAllAlerts()
        }
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            iWeatherRepo.deleteAlert(alert)
            getAllAlerts()
        }
    }

    fun getAllAlerts() {
        viewModelScope.launch {
            iWeatherRepo.getListOfAlerts()
                .catch { e -> _alerts.value = UiState.Failed(e) }
                .collect {
                    _alerts.value = UiState.Success(it)
                }
        }
    }
}
