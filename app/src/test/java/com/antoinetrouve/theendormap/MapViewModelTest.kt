package com.antoinetrouve.theendormap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.antoinetrouve.theendormap.map.MapUiState
import com.antoinetrouve.theendormap.map.MapViewModel
import com.antoinetrouve.theendormap.poi.PoiRepositoryList
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MapViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // useful to test LiveData

    @Test
    fun `load POIs with invalid coordinates error`() {
        val viewModel = MapViewModel(PoiRepositoryList())
        val observer = viewModel.getUiState().testObserver()

        val latitude = -91.0
        val longitude = -181.0

        viewModel.loadPois(latitude, longitude)

//        assertEquals(1, observer.observedValues.size)
//        assertTrue(observer.observedValues[0] is MapUiState.Error)
        assertEquals(
            listOf(
                MapUiState.Error("Invalid coordinates: lat=$latitude, long=$longitude")
            ),observer.observedValues
        )
    }
}