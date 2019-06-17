package com.davlop.skifun.data.source.remote.service.response

class GooglePlacesResponse(val results: List<GooglePlacesResultResponse>,
                           val next_page_token: String?)

class GooglePlacesResultResponse(val geometry: GooglePlacesGeometryResponse, val name: String,
                                 val place_id: String, val vicinity: String)

class GooglePlacesGeometryResponse(val location: GooglePlacesLocationResponse)

class GooglePlacesLocationResponse(val lat: Double, val lng: Double)