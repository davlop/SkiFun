package com.davlop.skifun.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "WeatherItems")
data class WeatherItem(
        @PrimaryKey @ColumnInfo(name = "_id") val id: String,
        @ColumnInfo(name = "city_name") val cityName: String,
        @ColumnInfo(name = "time") val time: String,
        @ColumnInfo(name = "weather") val weather: String,
        @ColumnInfo(name = "weather_description") val weatherDescription: String,
        @ColumnInfo(name = "temperature") val temperature: Double,
        @ColumnInfo(name = "rain") val rain: Double?,
        @ColumnInfo(name = "snow") val snow: Double?
)
