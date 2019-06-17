package com.davlop.skifun.data.source.local

import com.davlop.skifun.data.model.WeatherItem
import com.davlop.skifun.data.source.local.room.WeatherItemsDao
import com.davlop.skifun.utils.DateTimeUtils
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WeatherItemsLocalDataSource @Inject constructor(private val dao: WeatherItemsDao) {

    fun getWeatherForecast(cityName: String): Flowable<List<WeatherItem>> =
            dao.getForecast(cityName)
                    .subscribeOn(Schedulers.io())

    fun insertAll(items: List<WeatherItem>) = Completable.fromAction { dao.insertAll(items) }

    fun deleteOldWeather() = Completable.fromAction {
        dao.deleteOldWeather(DateTimeUtils.getNowFormatted())
    }

}