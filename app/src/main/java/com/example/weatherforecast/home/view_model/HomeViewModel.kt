    package com.example.weatherforecast.home.view_model

    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.weatherforecast.model.dto.WeatherResponse
    import com.example.weatherforecast.model.repository.IWeatherRepository
    import com.example.weatherforecast.model.repository.WeatherRepository
    import com.example.weatherforecast.utilities.ApiState
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.catch
    import kotlinx.coroutines.launch

    class HomeViewModel(
        private val iWeatherRepo: IWeatherRepository
    ) : ViewModel() {

        private val _weatherMutableStateFlow = MutableStateFlow<ApiState<WeatherResponse>>(ApiState.Loading<WeatherResponse>())

        val weatherStateFlow: StateFlow<ApiState<WeatherResponse>> = _weatherMutableStateFlow
        fun getCurrentWeather(lat: Double, lon: Double) {
            viewModelScope.launch(Dispatchers.IO) {
                iWeatherRepo.getCurrentWeather(lat, lon)
                    .catch { _weatherMutableStateFlow.value = ApiState.Failed(it) }
                    .collect {
                        _weatherMutableStateFlow.value = ApiState.Success(it)
                    }
            }
        }

        fun getFavoriteWeather(lat :Double, long: Double){
            viewModelScope.launch(Dispatchers.IO) {
                iWeatherRepo.getFavouriteWeather(lat,long)
                    .catch { _weatherMutableStateFlow.value = ApiState.Failed(it) }
                    .collect{
                        Log.i("TAG", "onViewCreated: "+ it.timezone)
                        _weatherMutableStateFlow.value = ApiState.Success(it)
                    }
            }
        }

    }