    package com.example.weatherforecast.home.view_model

    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.weatherforecast.model.dto.WeatherResponse
    import com.example.weatherforecast.model.repository.IWeatherRepository
    import com.example.weatherforecast.utilities.UiState
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.catch
    import kotlinx.coroutines.launch

    class HomeViewModel(
        private val iWeatherRepo: IWeatherRepository
    ) : ViewModel() {

        private val _weatherMutableStateFlow = MutableStateFlow<UiState<WeatherResponse>>(UiState.Loading<WeatherResponse>())

        val weatherStateFlow: StateFlow<UiState<WeatherResponse>> = _weatherMutableStateFlow
        fun getCurrentWeather(lat: Double, lon: Double) {
            viewModelScope.launch(Dispatchers.IO) {
                iWeatherRepo.getCurrentWeather(lat, lon)
                    .catch { _weatherMutableStateFlow.value = UiState.Failed(it) }
                    .collect {
                        _weatherMutableStateFlow.value = UiState.Success(it)
                    }
            }
        }

        fun getFavoriteWeather(lat :Double, long: Double){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    iWeatherRepo.getFavouriteWeather(lat, long)
                        .catch { e -> throw e }
                        .collect {
                            _weatherMutableStateFlow.value = UiState.Success(it)
                        }
                } catch (e: Exception) {
                    _weatherMutableStateFlow.value = UiState.Failed(e)
                }
            }
        }

    }