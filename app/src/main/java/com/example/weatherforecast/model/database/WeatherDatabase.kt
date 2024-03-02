package com.example.weatherforecast.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.model.dto.Current
import com.example.weatherforecast.model.dto.WeatherResponse

//@Database(entities = arrayOf(WeatherResponse::class), version = 1)
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun getWeather() : WeatherDao
    companion object{
        @Volatile
        private var instance : WeatherDatabase? = null
        fun getInstance(context: Context) : WeatherDatabase{
            return instance ?: synchronized(this){
                val dbInstance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java, "weatherDb")
                    .build()
                instance = dbInstance
                dbInstance
            }
        }
    }
}