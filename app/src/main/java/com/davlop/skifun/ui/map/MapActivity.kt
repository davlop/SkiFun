package com.davlop.skifun.ui.map

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.davlop.skifun.R
import com.davlop.skifun.data.model.*
import com.davlop.skifun.ui.resort.ResortActivity
import com.davlop.skifun.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import dagger.android.AndroidInjection
import javax.inject.Inject
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var map: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private lateinit var clusterManager: ClusterManager<SkiMarker>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapViewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MapViewModel::class.java)

        // Load Google Maps fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        moveToDefaultLocation()
        setUpClusterManager()
    }

    private fun moveToDefaultLocation() {
        mapViewModel.getRegionById(SPAIN_ID, object : SkiPlaceCallback {
            override fun onSkiPlaceLoaded(skiPlace: SkiPlace) {
                // at this point, a RegionSkiPlace is returned (Spain in this case)
                moveCameraToRegion(skiPlace as RegionSkiPlace)
                loadRegionSubMarkers(skiPlace)
            }
        })
    }

    private fun setUpClusterManager() {
        clusterManager = ClusterManager(this, map)
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        clusterManager.renderer = SkiIconRenderer(this, map, clusterManager)

        // zoom in when a cluster is clicked
        clusterManager.setOnClusterClickListener {
            val boundaries = calculateClusterZoomLatLngBounds(it.items)
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundaries, 500, 300, 0))
            true
        }

        // navigate to a ResortsActivity when a cluster item is clicked
        clusterManager.setOnClusterItemClickListener {
            val resort = it.resort
            intent = Intent(this@MapActivity, ResortActivity::class.java)
            intent.putExtra(getString(R.string.extra_key_resortname), resort.name)
            intent.putExtra(getString(R.string.extra_key_resortId), resort.id)
            intent.putExtra(getString(R.string.extra_key_parentId), SPAIN_ID)
            startActivity(intent)
            true
        }
    }

    // returns a LatLngBounds based on the argument's highest northeast & southwest values
    private fun calculateClusterZoomLatLngBounds(markers: Collection<SkiMarker>): LatLngBounds {

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

        // add "extra" latitude and longitude so markers don't touch borders
        val southwest = LatLng(lowestLatitude - 0.05, lowestLongitude - 0.05)
        val northeast = LatLng(highestLatitude + 0.05, highestLongitude + 0.05)
        return LatLngBounds(southwest, northeast)
    }

    // adds regions's resorts to the cluster manager
    private fun loadRegionSubMarkers(region: RegionSkiPlace) {
        mapViewModel.getResortsByParentId(region.id, object : SkiPlaceCallback {
            override fun onSkiPlaceLoaded(skiPlace: SkiPlace) {
                clusterManager.addItem(SkiMarker(skiPlace as ResortSkiPlace))
                clusterManager.cluster()
            }
        })
    }

    private fun moveCameraToRegion(region: RegionSkiPlace) {
        val boundaries = LatLngBounds(
                LatLng(region.leftboundaries.latitude, region.leftboundaries.longitude),
                LatLng(region.rightboundaries.latitude, region.rightboundaries.longitude))
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(boundaries, 500, 300, 0))
    }

    override fun onBackPressed() {
        // do nothing to prevent the user from exiting the app when pressing the back button
    }

    companion object {
        const val SPAIN_ID = "obfuscated"
    }
}

interface SkiPlaceCallback {
    fun onSkiPlaceLoaded(skiPlace: SkiPlace)
}