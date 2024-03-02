package com.example.weatherforecast.model.dto

import androidx.room.Entity
data class WeatherResponse(
	val alerts: List<AlertsItem?>? = null,
	val current: Current? = null,
	val timezone: String? = null,
	val timezoneOffset: Int? = null,
	val daily: List<DailyItem?>? = null,
	val lon: Double? = null,
	val hourly: List<HourlyItem?>? = null,
	val minutely: List<MinutelyItem?>? = null,
	val lat: Double? = null
)
