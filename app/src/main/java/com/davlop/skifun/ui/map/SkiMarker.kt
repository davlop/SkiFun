package com.davlop.skifun.ui.map

import android.content.Context
import android.graphics.Color
import android.support.v4.content.res.ResourcesCompat
import com.davlop.skifun.R
import com.davlop.skifun.data.model.ResortSkiPlace
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator

// represents a ResortSkiPlace as a map's ClusterItem
class SkiMarker(val resort: ResortSkiPlace) : ClusterItem {

    override fun getSnippet(): String = ""

    override fun getTitle(): String = resort.name

    override fun getPosition(): LatLng =
            LatLng(resort.coordinates.latitude, resort.coordinates.longitude)

}

// icon renderer class for SkiMarker
class SkiIconRenderer(context: Context, map: GoogleMap, clusterManager: ClusterManager<SkiMarker>) :
        DefaultClusterRenderer<SkiMarker>(context, map, clusterManager) {

    private val iconGenerator: IconGenerator = IconGenerator(context)

    init {
        iconGenerator.setBackground(ResourcesCompat.getDrawable(context.resources, R.drawable
                .amu_bubble_mask, null))
        iconGenerator.setColor(Color.WHITE)
        iconGenerator.setTextAppearance(R.style.marker_text)
    }

    override fun onBeforeClusterItemRendered(item: SkiMarker?, markerOptions: MarkerOptions?) {
        markerOptions?.icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(item?.title)))
    }

    // cluster every marker that is not alone
    override fun shouldRenderAsCluster(cluster: Cluster<SkiMarker>?): Boolean {
        if (cluster != null) return cluster.size > 1
        else return false
    }
}