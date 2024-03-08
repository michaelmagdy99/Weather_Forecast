package com.example.weatherforecast.sharedprefernces

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherforecast.utilities.SettingsConstants

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =context.getSharedPreferences("settings",Context.MODE_PRIVATE)
    val editor=sharedPreferences.edit()


    companion object {
        @Volatile
        private var instance: SharedPreferencesHelper? = null

        fun getInstance(context: Context): SharedPreferencesHelper {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferencesHelper(context).also { instance = it }
            }
        }
    }

    fun saveCurrentLocation(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun loadCurrentLocation(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun insertInData()
    {
        if(SettingsConstants.location !=null)
        {
            val res=if(SettingsConstants.location == SettingsConstants.Location.GPS) 0 else 1
            editor.putInt("location",res)
        }
        if(SettingsConstants.temperature !=null)
        {
            val res= when (SettingsConstants.temperature) {
                SettingsConstants.Temperature.C -> { 0 }
                SettingsConstants.Temperature.K -> { 1 }
                else -> { 2 }
            }
            editor.putInt("temperature",res)
        }
        if(SettingsConstants.lang !=null)
        {
            val res=if(SettingsConstants.lang == SettingsConstants.Lang.AR) 0 else 1
            editor.putInt("lang",res)
        }
        if(SettingsConstants.windSpeed !=null)
        {
            val res=if(SettingsConstants.windSpeed == SettingsConstants.WindSpeed.M_S) 0 else 1
            editor.putInt("wSpeed",res)
        }
        if(SettingsConstants.notificationState !=null)
        {
            val res=if(SettingsConstants.notificationState == SettingsConstants.NotificationState.OFF) 0 else 1
            editor.putInt("notificationState",res)
        }
        editor.apply()
    }
    fun loadData(){
        SettingsConstants.location =
            if(sharedPreferences.getInt("location",-1) == 0){
                SettingsConstants.Location.GPS
            }else{
                SettingsConstants.Location.MAP
            }
        SettingsConstants.lang =  if(sharedPreferences.getInt("lang",-1)==0){
            SettingsConstants.Lang.AR
        }else{
            SettingsConstants.Lang.EN
        }
        SettingsConstants.windSpeed =if(sharedPreferences.getInt("wSpeed",-1)==0){
            SettingsConstants.WindSpeed.M_S
        }else{
            SettingsConstants.WindSpeed.MILE_HOUR
        }
        SettingsConstants.temperature =when(sharedPreferences.getInt("temperature",-1))
        {
            0-> SettingsConstants.Temperature.C
            1-> SettingsConstants.Temperature.K
            else-> SettingsConstants.Temperature.F
        }
        SettingsConstants.notificationState =if(sharedPreferences.getInt("notificationState",-1)==0)
        {
            SettingsConstants.NotificationState.OFF
        }else{
            SettingsConstants.NotificationState.ON
        }
    }

    fun isNewSettingsRestart():Int{
        return sharedPreferences.getInt("isNewSetting",-1)
    }
    fun saveAsNewSetting(num:Int){
        val editor= sharedPreferences.edit()
        editor.putInt("isNewSetting", num)
        editor.apply()
    }
}
