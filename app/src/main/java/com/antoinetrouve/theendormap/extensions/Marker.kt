package com.antoinetrouve.theendormap.extensions

import android.graphics.Color
import com.antoinetrouve.theendormap.poi.Poi
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

// TODO : use extension
fun Poi.addPoiToMapMarker(poi: Poi, map: GoogleMap) : Marker {
    val options = MarkerOptions()
        .position(LatLng(poi.latitude, poi.longitude))
        .title(poi.title)
        .snippet(poi.description)

    // Config an icon marker or an icon color according to poi
    if (poi.iconId > 0) {
        options.icon(BitmapDescriptorFactory.fromResource(poi.iconId))
    } else if (poi.iconColor != 0) {
        val hue = when (poi.iconColor) {
            Color.BLUE -> BitmapDescriptorFactory.HUE_AZURE
            Color.GREEN -> BitmapDescriptorFactory.HUE_GREEN
            Color.YELLOW -> BitmapDescriptorFactory.HUE_YELLOW
            Color.RED -> BitmapDescriptorFactory.HUE_RED
            else -> BitmapDescriptorFactory.HUE_RED
        }
        options.icon(BitmapDescriptorFactory.defaultMarker(hue))
    }

    val marker = map.addMarker(options)
    marker.tag = poi

    return marker
}