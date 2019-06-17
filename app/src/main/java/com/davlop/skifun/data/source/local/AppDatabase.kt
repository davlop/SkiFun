package com.davlop.skifun.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.davlop.skifun.data.model.WeatherItem
import com.davlop.skifun.data.source.local.room.WeatherItemsDao

@Database(entities = [WeatherItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun weatherItemsDao(): WeatherItemsDao

}
