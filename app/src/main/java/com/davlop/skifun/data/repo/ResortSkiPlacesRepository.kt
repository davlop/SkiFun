package com.davlop.skifun.data.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class ResortSkiPlacesRepository @Inject constructor(private val database: FirebaseFirestore,
                                                    private val storage: FirebaseStorage) {

    fun getResortById(resortId: String, parentId: String): Task<DocumentSnapshot> =
            database.collection(COLLECTION_ROOT)
                    .document(DOCUMENT_RESORTS)
                    .collection(parentId)
                    .document(resortId)
                    .get()

    fun getResortsByParentId(id: String): Task<QuerySnapshot> =
            database.collection(COLLECTION_ROOT)
                    .document(DOCUMENT_RESORTS)
                    .collection(id)
                    .get()

    fun getResortsTrails(resortId: String, parentId: String): Task<QuerySnapshot> =
            database.collection(COLLECTION_ROOT)
                    .document(DOCUMENT_RESORTS)
                    .collection(parentId)
                    .document(resortId)
                    .collection(COLLECTION_TRAILS)
                    .get()

    fun getPistesImage(url: String?): StorageReference? {
        var reference: StorageReference? = null

        if (!url.isNullOrEmpty()) {
            reference = storage.getReferenceFromUrl(url!!)
        }

        return reference
    }

    companion object {
        const val COLLECTION_ROOT = "obfuscated"
        const val COLLECTION_TRAILS = "obfuscated"
        const val DOCUMENT_RESORTS = "obfuscated"
    }

}