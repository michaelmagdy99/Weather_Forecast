package com.example.weatherforecast.model.dto

data class DailyItem(
	val moonset: Int? = null,
	val sunrise: Int? = null,
	val temp: Temp? = null,
	val moonPhase: Any? = null,
	val uvi: Any? = null,
	val moonrise: Int? = null,
	val pressure: Int? = null,
	val clouds: Int? = null,
	val feelsLike: FeelsLike? = null,
	val windGust: Any? = null,
	val dt: Long? = null,
	val pop: Double? = null,
	val windDeg: Int? = null,
	val dewPoint: Any? = null,
	val sunset: Int? = null,
	val weather: List<WeatherItem?>? = null,
	val humidity: Int? = null,
	val windSpeed: Any? = null,
	val rain: Any? = null
)
