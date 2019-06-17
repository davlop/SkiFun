package com.davlop.skifun.data.model

import com.google.firebase.firestore.GeoPoint

// default values are necessary for Firestore to deserialize object
open class RegionSkiPlace(
        override var id: String = "",
        override val name: String = "",
        val leftboundaries: GeoPoint = GeoPoint(0.0, 0.0),
        val rightboundaries: GeoPoint = GeoPoint(0.0, 0.0)
) : SkiPlace {

    override fun toString(): String {
        return "RegionSkiPlace(id='$id', name='$name', leftboundaries=$leftboundaries, rightboundaries=$rightboundaries)"
    }

}