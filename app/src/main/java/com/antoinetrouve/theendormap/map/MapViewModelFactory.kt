package com.antoinetrouve.theendormap.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.antoinetrouve.theendormap.poi.PoiRepository

@Suppress("UNCHECKED_CAST")
class MapViewModelFactory(private val poiRepository: PoiRepository) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapViewModel(poiRepository) as T
    }

}