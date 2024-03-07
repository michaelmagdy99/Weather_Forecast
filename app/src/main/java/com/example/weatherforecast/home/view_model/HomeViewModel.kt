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
    class HomeViewModel(
        private val iWeatherRepo: WeatherRepository,
        private val context: Context
    ) : ViewModel() {

        private val _weatherMutableStateFlow = MutableStateFlow<ApiState<WeatherResponse>>(ApiState.Loading<WeatherResponse>())

        val weatherStateFlow: StateFlow<ApiState<WeatherResponse>> = _weatherMutableStateFlow

        private val _addressStateFlow = MutableStateFlow<String>("")
        val addressStateFlow: StateFlow<String> = _addressStateFlow

        init {
            getCurrentWeather()
        }


        private fun getCurrentWeather(){
            viewModelScope.launch(Dispatchers.IO) {
                val lat = SharedPreferencesHelper.getInstance(context).loadData("latitude")?.toDouble() ?: 0.0
                val lon = SharedPreferencesHelper.getInstance(context).loadData("longitude")?.toDouble() ?: 0.0

                iWeatherRepo.getCurrentWeather(lat,lon,"en","metric")
                    .catch { _weatherMutableStateFlow.value = ApiState.Failed(it) }
                    .collect{
                        Log.i("TAG", "onViewCreated: "+ it.timezone)
                        _weatherMutableStateFlow.value = ApiState.Success(it)
                    }

                val address = getAddress(lat, lon)
                _addressStateFlow.value = address
            }
        }

         fun getAddress(lat: Double, lon: Double): String {
            val geocoder = Geocoder(context)
            val list = geocoder.getFromLocation(lat, lon, 1)
            return list?.get(0)?.countryName + ", "+ list?.get(0)?.adminArea ?: "UnKnown"
        }


    }