package com.example.weatherforecast.utilities

import android.content.Context
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.weatherforecast.R
import com.example.weatherforecast.sharedprefernces.SharedPreferencesHelper

object SettingsConstants {
    var location: Location?=Location.GPS
    var temperature: Temperature?=Temperature.C
    var lang: Lang?=Lang.EN
    var windSpeed: WindSpeed?=WindSpeed.M_S
    var notificationState: NotificationState?=null
    enum class Location{MAP,GPS}
    enum class Temperature{C,K,F}
    enum class Lang{AR,EN}
    enum class WindSpeed{M_S,MILE_HOUR}
    enum class NotificationState{ON,OFF}

    fun getLocation(): Int {
        return when (SettingsConstants.location) {
            SettingsConstants.Location.GPS -> {
                R.id.home
            }
            SettingsConstants.Location.MAP -> {
                R.id.map
            }
            else -> {
                R.id.chooseDialogFragment
            }
        }
    }

    fun getLang():String {

        if(lang==null) {
            return "en"
        }
        return if(lang==Lang.EN) {
            "en"
        }else{
            "ar"
        }
    }
    fun getTemp():Char{
        return if (temperature == Temperature.F) {
            'F'
        }else if(temperature==Temperature.K) {
            'K'
        }
        else  {
            'C'
        }
    }

    fun getWindSpeed(): String {
        var res=""
        res = if(getLang()=="en") {
            if(windSpeed==WindSpeed.MILE_HOUR)" Mile/H" else "M/S"
        }else{
            if(windSpeed==WindSpeed.MILE_HOUR)" ميل/ساعة" else "متر/ث"
        }
        return res
    }
}