package com.example.weatherforecast.model.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.Serializable

@Entity(tableName = "fav_table")
data class FaviourateLocationDto(
     @PrimaryKey val locationKey: LocationKey,
     val countryName: String,
     val temp: String
)

data class LocationKey(
     val lat: Double,
     val long: Double
)

class LocationKeyConverter {
     @TypeConverter
     fun fromLocationKey(locationKey: LocationKey): String {
          return "${locationKey.lat},${locationKey.long}"
     }

     @TypeConverter
     fun toLocationKey(value: String): LocationKey {
          val (lat, long) = value.split(",")
          return LocationKey(lat.toDouble(), long.toDouble())
     }
}