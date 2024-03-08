    package com.example.weatherforecast.home.view_model

    import android.content.Context
    import android.location.Geocoder
    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.example.weatherforecast.model.dto.WeatherResponse
    import com.example.weatherforecast.model.repository.WeatherRepository
    import com.example.weatherforecast.utilities.ApiState
    import com.example.weatherforecast.utilities.SharedPreferencesHelper
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.catch
    import kotlinx.coroutines.launch
    import java.util.concurrent.TimeUnit

    class HomeViewModel(
        private val iWeatherRepo: WeatherRepository
    ) : ViewModel() {

        private val _weatherMutableStateFlow = MutableStateFlow<ApiState<WeatherResponse>>(ApiState.Loading<WeatherResponse>())

        val weatherStateFlow: StateFlow<ApiState<WeatherResponse>> = _weatherMutableStateFlow

        private val _addressStateFlow = MutableStateFlow<String>("")
        val addressStateFlow: StateFlow<String> = _addressStateFlow


        fun getCurrentWeather(){
            viewModelScope.launch(Dispatchers.IO) {
                iWeatherRepo.getCurrentWeather()
                    .catch { _weatherMutableStateFlow.value = ApiState.Failed(it) }
                    .collect{
                        Log.i("TAG", "onViewCreated: "+ it.timezone)
                        _weatherMutableStateFlow.value = ApiState.Success(it)
                    }
            }
        }


         fun getFavoriteWeather(lat :Double, long: Double){
            viewModelScope.launch(Dispatchers.IO) {
                iWeatherRepo.getFavouriteWeather(lat,long,"en","metric")
                    .catch { _weatherMutableStateFlow.value = ApiState.Failed(it) }
                    .collect{
                        Log.i("TAG", "onViewCreated: "+ it.timezone)
                        _weatherMutableStateFlow.value = ApiState.Success(it)
                    }
            }
        }
    }