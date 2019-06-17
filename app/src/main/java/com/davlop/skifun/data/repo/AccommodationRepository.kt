package com.davlop.skifun.data.repo

import com.davlop.skifun.data.model.Hotel
import com.davlop.skifun.data.source.remote.service.GooglePlacesAPI
import com.davlop.skifun.data.source.remote.service.response.GooglePlacesResultResponse
import com.google.firebase.firestore.GeoPoint
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AccommodationRepository @Inject constructor(private val api: GooglePlacesAPI) {

    fun getAccommodationPlaces(coordinates: GeoPoint): Single<List<Hotel>> {
        val list = mutableListOf<Hotel>()
        val latLngString = "${coordinates.latitude},${coordinates.longitude}"

        return api.getAccommodationPlaces(latLngString, "obfuscated")
                .flatMap {
                    it.results.forEach { addHotelToList(list, it) }
                    if (it.next_page_token != null) {
                        fetchNextPage(list, it.next_page_token)
                    } else {
                        Single.just(list)
                    }
                }.subscribeOn(Schedulers.io())
    }

    // need to the delay the call so Google's server doesn't reject it
    private fun fetchNextPage(list: MutableList<Hotel>, token: String): Single<List<Hotel>> =
        Single.timer(1800, TimeUnit.MILLISECONDS)
                .flatMap {
                    api.getResponseNextPage(token, "obfuscated")
                            .flatMap {
                                it.results.forEach { addHotelToList(list, it) }
                                if (it.next_page_token != null) {
                                    fetchNextPage(list, it.next_page_token)
                                } else {
                                    Single.just(list)
                                }
                            }
                }.subscribeOn(Schedulers.io())

    private fun addHotelToList(list: MutableList<Hotel>, response: GooglePlacesResultResponse) {
        val latitude = response.geometry.location.lat
        val longitude = response.geometry.location.lng

        list.add(Hotel(response.place_id, response.name, GeoPoint(latitude, longitude), response.vicinity))
    }

}