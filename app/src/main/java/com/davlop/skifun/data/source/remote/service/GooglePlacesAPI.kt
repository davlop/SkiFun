package com.davlop.skifun.data.source.remote.service

import com.davlop.skifun.data.source.remote.service.response.GooglePlacesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesAPI {

    @GET("/maps/api/place/nearbysearch/json?parameters&type=lodging&radius=7000")
    fun getAccommodationPlaces(
            @Query("location") location: String,
            @Query("key") apiKey: String
            ) : Single<GooglePlacesResponse>

    @GET("/maps/api/place/nearbysearch/json?parameters")
    fun getResponseNextPage(
            @Query("pagetoken") pageToken: String,
            @Query("key") apiKey: String
            ) : Single<GooglePlacesResponse>

}