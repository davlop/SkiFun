package com.davlop.skifun.data.model

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

// default values are necessary for Firestore to deserialize object
class ResortSkiPlace(
        override val coordinates: GeoPoint = GeoPoint(0.0, 0.0),
        override val name: String = "",
        override var id: String = "",
        val elevation_high: Int? = null,
        val elevation_low: Int? = null,
        val parent: DocumentReference? = null,
        val pistesimage: String = "",
        val phone: String? = null,
        val price_adults: Int? = null,
        val price_kids: Int? = null,
        var trails: HashMap<Int,TrailType>? = null,
        val website: String? = null
) : CoordSkiPlace {

    override fun toString(): String {
        return "ResortSkiPlace(coordinates=$coordinates, name='$name', id='$id', elevation_high=$elevation_high, elevation_low=$elevation_low, parent=$parent, phone=$phone, price_adults=$price_adults, price_kids=$price_kids, website='$website')"
    }

}