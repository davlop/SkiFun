package com.davlop.skifun.data.source.local.room

import android.arch.persistence.room.*
import com.davlop.skifun.data.model.WeatherItem
import io.reactivex.Flowable

@Dao
abstract class WeatherItemsDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE city_name = :cityName")
    abstract fun getForecast(cityName: String): Flowable<List<WeatherItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertAll(items: List<WeatherItem>)

    @Delete
    abstract fun delete(item: WeatherItem)

    @Query("DELETE FROM $TABLE_NAME WHERE time < :now")
    abstract fun deleteOldWeather(now: String)

    companion object {
        const val TABLE_NAME = "WeatherItems"
    }

}
