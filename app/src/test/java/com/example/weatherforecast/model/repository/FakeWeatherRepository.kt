package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.database.FakeWeatherLocalDataSource
import com.example.weatherforecast.model.database.IWeatherLocalDataSource
import com.example.weatherforecast.model.database.WeatherLocalDataSource
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.model.remote.FakeWeatherRemoteDataSource
import com.example.weatherforecast.model.remote.IWeatherRemoteDataSource
import com.example.weatherforecast.model.remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherRepository : IWeatherRepository {

    private val fakeLocalDataSource: IWeatherLocalDataSource = FakeWeatherLocalDataSource()
    private val fakeRemoteDataSource: IWeatherRemoteDataSource = FakeWeatherRemoteDataSource()

    private val currentWeatherFlow = MutableStateFlow<WeatherResponse?>(null)

    fun setCurrentWeather(weatherResponse: WeatherResponse) {
        currentWeatherFlow.value = weatherResponse
    }
    override fun getCurrentWeather(lat: Double, lon: Double): Flow<WeatherResponse> {
        return fakeRemoteDataSource.getCurrentWeather(lat, lon)
    }

    override fun getLocalAllLocation(): Flow<List<FaviourateLocationDto>> {
        return fakeLocalDataSource.getAllLocation()
    }

    override suspend fun insertLocation(location: FaviourateLocationDto) {
        fakeLocalDataSource.insertLocation(location)
    }

    override suspend fun deleteLocation(location: FaviourateLocationDto) {
        fakeLocalDataSource.deleteLocation(location)
    }

    override fun getFavouriteWeather(lat: Double, lon: Double): Flow<WeatherResponse> {
        return fakeRemoteDataSource.getCurrentWeather(lat, lon)
    }

    override suspend fun insertAlert(alert: Alert) {
         fakeLocalDataSource.insertAlert(alert)
    }

    override suspend fun deleteAlert(alert: Alert) {
         fakeLocalDataSource.deleteAlert(alert)
    }

    override fun getListOfAlerts(): Flow<List<Alert>> {
         return fakeLocalDataSource.getListOfAlerts()
    }

    override fun getAlertWithId(id: String?): Alert {
         return fakeLocalDataSource.getAlertWithId(id!!)
    }
}
