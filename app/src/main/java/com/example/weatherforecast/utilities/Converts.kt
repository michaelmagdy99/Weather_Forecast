package com.example.weatherforecast.utilities

import kotlin.math.roundToInt

object Converts {

    fun getTemperature(temp:Int):Int{
        return when (SettingsConstants.temperature) {
            SettingsConstants.Temperature.F -> {
                temp*(9/5)+32
            }
            SettingsConstants.Temperature.K -> {
                temp+273
            }
            else -> {
                temp
            }
        }
    }
    fun getWindSpeed(w: Int?):String{
        return if(SettingsConstants.windSpeed==SettingsConstants.WindSpeed.MILE_HOUR)
        {
            "${(w?.div(1609.344))?.roundToInt() ?: 0}"
        }else{
            "${w ?: 0}"
        }
    }
}