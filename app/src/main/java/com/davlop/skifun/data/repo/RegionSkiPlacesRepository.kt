package com.davlop.skifun.data.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class RegionSkiPlacesRepository @Inject constructor(
        private val database: FirebaseFirestore) {

    fun getRegionById(id: String): Task<DocumentSnapshot> =
        database.collection(COLLECTION_ROOT)
                .document(DOCUMENT_REGIONPLACES)
                .collection(COLLECTION_REGIONS)
                .document(id)
                .get()

    companion object {
        const val COLLECTION_REGIONS = "obfuscated"
        const val COLLECTION_ROOT = "obfuscated"
        const val DOCUMENT_REGIONPLACES = "obfuscated"
    }

}