package com.davlop.skifun.data.model

import com.google.firebase.firestore.GeoPoint

class Hotel(
        override var id: String,
        override val name: String,
        override val coordinates: GeoPoint,
        val address: String
) : CoordSkiPlace {

    override fun toString(): String {
        return "Hotel(id='$id', name='$name', coordinates=$coordinates, address='$address')"
    }

}