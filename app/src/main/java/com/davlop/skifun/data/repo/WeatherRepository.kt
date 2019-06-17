package com.davlop.skifun.data.repo

import com.davlop.skifun.data.source.local.WeatherItemsLocalDataSource
import com.davlop.skifun.data.source.remote.WeatherItemsRemoteDataSource
import com.google.firebase.firestore.GeoPoint
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WeatherRepository @Inject constructor(
        private val localDataSource: WeatherItemsLocalDataSource,
        private val remoteDataSource: WeatherItemsRemoteDataSource) {

    fun refreshTransactions(coordinates: GeoPoint, cityName: String): Completable =
            remoteDataSource.getWeatherForecast(coordinates, cityName)
                    .subscribeOn(Schedulers.io())
                    .flatMapCompletable { localDataSource.insertAll(it.filterNotNull()) }
                    .andThen(localDataSource.deleteOldWeather())

    fun getWeatherForecast(cityName: String) = localDataSource.getWeatherForecast(cityName)

}