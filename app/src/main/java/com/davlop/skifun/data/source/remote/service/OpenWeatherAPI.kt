package com.davlop.skifun.data.source.remote.service

import com.davlop.skifun.data.source.remote.service.response.OpenWeatherRootResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherAPI {

    @GET("/data/2.5/forecast?units=metric")
    fun getWeatherForecast(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double,
            @Query("APPID") apiKey: String
    ) : Single<OpenWeatherRootResponse>

}