package com.antoinetrouve.theendormap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

sealed class MapUiState {
    object Loading : MapUiState()
    data class Error(val errorMessage: String) : MapUiState()
    data class PoiReady(val userPoi: Poi? = null, val pois: List<Poi>? = null) : MapUiState()
}

class MapViewModel : ViewModel() {
    private val uiState by lazy { MutableLiveData<MapUiState>() }
    fun getUiState() : LiveData<MapUiState> = uiState
}