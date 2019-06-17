package com.davlop.skifun.data.source.remote

import com.davlop.skifun.data.model.WeatherItem
import com.davlop.skifun.data.source.remote.service.OpenWeatherAPI
import com.davlop.skifun.utils.DateTimeUtils
import com.google.firebase.firestore.GeoPoint
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WeatherItemsRemoteDataSource @Inject constructor(private val api: OpenWeatherAPI) {

    fun getWeatherForecast(coordinates: GeoPoint, cityName: String): Flowable<List<WeatherItem?>> =
            api.getWeatherForecast(coordinates.latitude, coordinates.longitude,
                    "obfuscated")
                    .map {
                        it.list.map {
                            val mainResponse = it.main
                            val weatherResponse = it.weather.first()
                            val rainResponse = it.rain
                            val snowResponse = it.snow

                            // only store today's, tomorrow's, and other "important" items
                            if (filterWeatherItemsByTime(it.dt_txt)) {

                                WeatherItem(generateId(cityName, it.dt_txt), cityName, it.dt_txt,
                                        weatherResponse.main, weatherResponse.description,
                                        mainResponse.temp, rainResponse?.threeh,
                                        snowResponse?.threeh)
                            } else {
                                null
                            }
                        }
                    }.toFlowable()
                    .subscribeOn(Schedulers.io())

    private fun filterWeatherItemsByTime(utcTime: String): Boolean {
        return DateTimeUtils.checkIfStringIsToday(utcTime) ||
                DateTimeUtils.checkIfStringIsTomorrow(utcTime) ||
                DateTimeUtils.checkIfStringIsImportantTime(utcTime)
    }

    // generates a unique id based on the city name and the date time
    private fun generateId(cityName: String, dateTime: String): String {
        val dateTrimmed = dateTime.replace("-", "").replace(":", "").replace(" ", "")
        return cityName.replace(" ", "") + dateTrimmed
    }

}
