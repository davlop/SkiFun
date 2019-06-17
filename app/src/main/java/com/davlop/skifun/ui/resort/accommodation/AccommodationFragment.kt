package com.davlop.skifun.ui.resort.accommodation

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.davlop.skifun.R
import com.davlop.skifun.data.model.Hotel
import com.davlop.skifun.data.model.ResortSkiPlace
import com.davlop.skifun.ui.resort.ResortCallback
import com.davlop.skifun.viewmodel.ResortActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_accomodation.*
import timber.log.Timber
import javax.inject.Inject

class AccommodationFragment : Fragment(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var map: GoogleMap
    private lateinit var resortActivityViewModel: ResortActivityViewModel
    private lateinit var clusterManager: ClusterManager<HotelMarker>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_accomodation, container, false)

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.let {
            resortActivityViewModel = ViewModelProviders.of(activity!!, viewModelFactory)
                    .get(ResortActivityViewModel::class.java)
        }

        // Load Google Maps fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.accommodation_map) as
                SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        setUpClusterManager()

        // change the map style to not show any Point of interest, so they don't overlap
        // with custom hotel markers
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))

        resortActivityViewModel.loadResort(object : ResortCallback {
            override fun onResortLoaded(resort: ResortSkiPlace?) {
                if (resort != null) {
                    val position = LatLng(resort.coordinates.latitude,
                            resort.coordinates.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 11.5f))
                    loadHotelMarkers()
                }
            }
        })
    }

    private fun setUpClusterManager() {
        clusterManager = ClusterManager(context, map)
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        context?.let {
            clusterManager.renderer = HotelIconRenderer(context!!, map, clusterManager)
        }

        // zoom in when a cluster is clicked
        clusterManager.setOnClusterClickListener {
            val boundaries = calculateClusterZoomLatLngBounds(it.items)
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundaries, 500, 300, 0))
            true
        }
    }

    // returns a LatLngBounds based on the argument's highest northeast & southwest values
    private fun calculateClusterZoomLatLngBounds(markers: Collection<HotelMarker>): LatLngBounds {

        var highestLatitude = markers.first().position.latitude
        var lowestLatitude = markers.first().position.latitude
        var highestLongitude = markers.first().position.longitude
        var lowestLongitude = markers.first().position.longitude

        markers.forEach {
            val markerLatitude = it.position.latitude
            val markerLongitude = it.position.longitude

            if (markerLatitude > highestLatitude) highestLatitude = markerLatitude
            else if (markerLatitude < lowestLatitude) lowestLatitude = markerLatitude
            if (markerLongitude > highestLongitude) highestLongitude = markerLongitude
            else if (markerLongitude < lowestLongitude) lowestLongitude = markerLongitude
        }

        val southwest = LatLng(lowestLatitude, lowestLongitude)
        val northeast = LatLng(highestLatitude, highestLongitude)
        return LatLngBounds(southwest, northeast)
    }

    private fun loadHotelMarkers() {
        resortActivityViewModel.getHotelsList()
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe (
                        { list : List<Hotel> ->
                            list.forEach { hotel ->
                                activity?.let {
                                    clusterManager.addItem(HotelMarker(hotel,
                                            it.getString(R.string.address),
                                            it.getString(R.string.no_address)))
                                }
                                clusterManager.cluster()
                            }
                            loading_view?.apply {
                                visibility = View.GONE
                            }
                        },
                        { Timber.w(it) }
                )
    }
}
