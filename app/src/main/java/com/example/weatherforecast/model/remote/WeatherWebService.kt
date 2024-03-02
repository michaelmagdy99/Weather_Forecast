package com.example.weatherforecast.model.remote

import com.example.weatherforecast.model.dto.WeatherResponse
import com.example.weatherforecast.utilities.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherWebService {

    @GET("onecall?")
    suspend fun getCurrentWeather(@Query("lat") lat: Double,
                                           @Query("lon") lon: Double,
                                           @Query("appid") appId:String = Constants.API_KEY,
                                           @Query("lang") lang:String,
                                           @Query("units") units:String ): Response<WeatherResponse>
}