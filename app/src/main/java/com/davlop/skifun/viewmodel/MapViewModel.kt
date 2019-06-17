package com.davlop.skifun.viewmodel

import android.arch.lifecycle.ViewModel
import com.davlop.skifun.data.model.RegionSkiPlace
import com.davlop.skifun.data.model.ResortSkiPlace
import com.davlop.skifun.data.repo.RegionSkiPlacesRepository
import com.davlop.skifun.data.repo.ResortSkiPlacesRepository
import com.davlop.skifun.ui.map.SkiPlaceCallback
import timber.log.Timber
import javax.inject.Inject

class MapViewModel @Inject constructor(
        private val resortsRepository: ResortSkiPlacesRepository,
        private val regionsRepository: RegionSkiPlacesRepository) : ViewModel() {

    fun getRegionById(id: String, callback: SkiPlaceCallback) = regionsRepository.getRegionById(id)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        Timber.d("TIMBER DocumentSnapshot data: %s", document.data!!)
                        val region = document.toObject(RegionSkiPlace::class.java)
                        region?.let {
                            it.id = document.id
                            callback.onSkiPlaceLoaded(region)
                        }
                    } else {
                        Timber.w("TIMBER No such document")
                    }
                } else {
                    Timber.e(it.exception, "TIMBER get() failed with ")
                }
            }

    fun getResortsByParentId(id: String, callback: SkiPlaceCallback) =
            resortsRepository.getResortsByParentId(id)
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    it.result.forEach {
                        Timber.d("TIMBER DocumentSnapshot data: %s", it.data)
                        val resort = it.toObject(ResortSkiPlace::class.java)
                        resort.id = it.id
                        callback.onSkiPlaceLoaded(resort)
                    }
                }

            }

}