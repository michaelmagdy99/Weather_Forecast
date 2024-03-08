package com.example.weatherforecast.utilities

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
    fun getLang():String
    {

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