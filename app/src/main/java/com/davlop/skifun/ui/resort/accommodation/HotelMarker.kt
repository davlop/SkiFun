package com.davlop.skifun.ui.resort.accommodation

import android.content.Context
import com.davlop.skifun.R
import com.davlop.skifun.data.model.Hotel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

// represents a Hotel as a map's ClusterItem
// constructor requires Strings representing "Address" & "Address not found" in the user's language
class HotelMarker(val hotel: Hotel, private val addressString: String,
                  private val noAddress: String) : ClusterItem {

    override fun getSnippet(): String {
        if (hotel.address.isBlank()) return noAddress
        else return addressString + " " + hotel.address
    }

    override fun getTitle(): String  = hotel.name

    override fun getPosition(): LatLng =
            LatLng(hotel.coordinates.latitude, hotel.coordinates.longitude)

}

// icon renderer class for HotelMarker
class HotelIconRenderer(context: Context, map: GoogleMap,
                        clusterManager: ClusterManager<HotelMarker>) :
        DefaultClusterRenderer<HotelMarker>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: HotelMarker?, markerOptions: MarkerOptions?) {
        markerOptions?.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_hotel))
    }

    // only cluster markers if there are +4 together
    override fun shouldRenderAsCluster(cluster: Cluster<HotelMarker>?): Boolean {
        if (cluster != null) return cluster.size > 4
        else return false
    }
}