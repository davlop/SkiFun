package com.davlop.skifun.data.model

// default values are necessary for Firestore to deserialize object
class TrailType(
        val difficulty: Int = 0,
        val totallength: Double? = null,
        val trailsnumber: Int = 0,
        val percentage: Int = 0
)