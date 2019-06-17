package com.davlop.skifun.data.source.remote.service.response

import com.google.gson.annotations.SerializedName

class OpenWeatherRootResponse(val list: List<OpenWeatherListResponse>)

class OpenWeatherListResponse(val dt_txt: String, val rain: OpenWeatherRainResponse?,
                              val snow: OpenWeatherSnowResponse?,
                              val main: OpenWeatherMainReponse,
                              val weather: List<OpenWeatherWeatherResponse>)

class OpenWeatherRainResponse(@SerializedName("3h") val threeh: Double)

class OpenWeatherSnowResponse(@SerializedName("3h") val threeh: Double)

class OpenWeatherMainReponse(val temp: Double)

class OpenWeatherWeatherResponse(val main: String, val description: String)