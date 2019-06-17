package com.davlop.skifun.data.model

import com.google.firebase.firestore.GeoPoint

interface CoordSkiPlace : SkiPlace {
    val coordinates: GeoPoint
}