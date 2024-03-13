package com.example.weatherforecast.model.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.io.Serializable
import java.util.UUID

@Entity(tableName = "Alert")
data class Alert(

    @PrimaryKey @ColumnInfo(name = "id") var id: String = UUID.randomUUID().toString(),
    val from: Long, val to: Long,
    val kind: String,
    val lon: Double,
    val lat: Double,

    ) : Serializable
