package com.davlop.skifun.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.Environment
import com.davlop.skifun.data.model.ResortSkiPlace
import com.davlop.skifun.data.model.WeatherItem
import com.davlop.skifun.data.repo.ResortSkiPlacesRepository
import com.davlop.skifun.data.repo.WeatherRepository
import com.davlop.skifun.ui.resort.ResortCallback
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.StorageReference
import io.reactivex.Completable
import io.reactivex.Flowable
import java.io.File
import javax.inject.Inject
import android.net.Uri
import com.davlop.skifun.data.model.Hotel
import com.davlop.skifun.data.model.TrailType
import com.davlop.skifun.data.repo.AccommodationRepository
import com.davlop.skifun.ui.resort.resorts.OnImageSavedListener
import com.davlop.skifun.ui.resort.resorts.TrailsCallback
import io.reactivex.Single

class ResortActivityViewModel @Inject constructor(
        private val repository: ResortSkiPlacesRepository,
        private val weatherRepository: WeatherRepository,
        private val accommodationRepository: AccommodationRepository) : ViewModel() {

    var parentId: String? = null
    var resortId: String? = null
    var resort: ResortSkiPlace? = null

    fun loadResort(callback: ResortCallback) {
        if (resort == null) {
            if (!resortId.isNullOrBlank() && !parentId.isNullOrBlank()) {
                repository.getResortById(resortId!!, parentId!!).addOnSuccessListener {
                    val resortConverted = it.toObject(ResortSkiPlace::class.java)
                    resort = resortConverted
                    callback.onResortLoaded(resort)
                }
            }
        } else {
            callback.onResortLoaded(resort)
        }
    }

    fun loadResortTrails(callback: TrailsCallback) {
        if (resort != null && !resortId.isNullOrBlank() && !parentId.isNullOrBlank()) {
            repository.getResortsTrails(resortId!!, parentId!!)
                    .addOnSuccessListener {
                        if (!it.isEmpty) {
                            val map = HashMap<Int, TrailType>()
                            it.documents.forEach {
                                val trailConverted = it.toObject(TrailType::class.java)
                                if (trailConverted != null) {
                                    map.put(trailConverted.difficulty, trailConverted)
                                }
                            }
                            resort!!.trails = map
                        }
                        callback.onTrailsReady()
                    }
        }
    }

    fun getWeatherForecast(): Flowable<List<WeatherItem>>? {
        if (resort == null) return null
        else return weatherRepository.getWeatherForecast(resort!!.name)
    }

    fun forceWeatherUpdate(): Completable? {
        if (resort == null) return null
        else return weatherRepository.refreshTransactions(resort!!.coordinates, resort!!.name)
    }

    fun getResortPistesImageReference(): StorageReference? {
        var reference: StorageReference? = null

        resort?.pistesimage?.let {
            reference = repository.getPistesImage(resort!!.pistesimage)
        }

        return reference
    }

    fun getHotelsList(): Single<List<Hotel>>? {
        if (resort == null) return null
        else return accommodationRepository.getAccommodationPlaces(resort!!.coordinates)
    }

    fun downloadPistesImage(successListener: OnSuccessListener<FileDownloadTask.TaskSnapshot>,
                            failureListener: OnFailureListener, saveListener : OnImageSavedListener) {
        val imageReference = getResortPistesImageReference()
        imageReference?.let {

            val localFile = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), createImageName(it.name))

            imageReference.getFile(localFile)
                    .addOnSuccessListener {
                        successListener.onSuccess(it)
                        saveListener.onImageReady(Uri.fromFile(localFile))
                    }
                    .addOnFailureListener { failureListener.onFailure(it) }
        }
    }

    private fun createImageName(imageName: String): String {
        return imageName.replace("min", "").replace('-', ' ').replace('_', ' ').capitalize()
    }

}