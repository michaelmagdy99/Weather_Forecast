package com.example.weatherforecast.model.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val service : WeatherWebService by lazy {
        retrofit.create(WeatherWebService::class.java)
    }
}