package com.example.weatherforecast.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.dto.Alert
import com.example.weatherforecast.model.dto.FaviourateLocationDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    //Fav Location
    @Query("SELECT * FROM fav_table")
    fun getAllLocation(): Flow<List<FaviourateLocationDto>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLocation(location: FaviourateLocationDto)

    @Query("DELETE FROM fav_table")
    fun deleteAllProduct()

    @Delete
    fun delete(location: FaviourateLocationDto)


    //Alerts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alert)
    @Delete
    suspend fun deleteAlert(alert: Alert)
    @Query("select * from Alert")
    fun getListOfAlerts():Flow<List<Alert>>

    @Query("select * from Alert where id = :id limit 1")
    fun getAlertWithId(id: String): Alert

    @Query("UPDATE Alert SET lat = :lat, lon= :lon WHERE id = :id")
    fun updateAlertItemLatLongById(id: String, lat: Double, lon: Double)

}