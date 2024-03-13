package com.example.weatherforecast.model.repository

import com.example.weatherforecast.model.database.FakeWeatherLocalDataSource
import com.example.weatherforecast.model.remote.FakeWeatherRemoteDataSource
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest{

    private lateinit var weatherRemoteDataSource: FakeWeatherRemoteDataSource
    private lateinit var weatherLocalDataSource: FakeWeatherLocalDataSource
    private lateinit var weatherRepo: WeatherRepository

    @Before
    fun setUpRepo() {
        weatherRemoteDataSource = FakeWeatherRemoteDataSource()
        weatherLocalDataSource = FakeWeatherLocalDataSource()

       // weatherRepo = WeatherRepository(weatherRemoteDataSource, weatherLocalDataSource)
    }

    @Test
    fun getCurrentWeather(){

    }

    @Test
    fun getFavouriteWeather(){

    }


    @Test
    fun getLocalAllLocation(){

    }

}