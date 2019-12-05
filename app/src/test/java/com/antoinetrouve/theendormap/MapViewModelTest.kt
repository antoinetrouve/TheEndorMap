package com.antoinetrouve.theendormap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.antoinetrouve.theendormap.map.MapUiState
import com.antoinetrouve.theendormap.map.MapViewModel
import com.antoinetrouve.theendormap.poi.Poi
import com.antoinetrouve.theendormap.poi.PoiRepository
import com.antoinetrouve.theendormap.poi.PoiRepositoryList
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MapViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // useful to test LiveData

    private val testPoi = Poi(
        title = "Hobbiton",
        latitude = 1.0,
        longitude = 1.0,
        imageId = R.drawable.hobbiton,
        detailUrl = "http://lotr.wikia.com/wiki/Hobbiton",
        description = """
                Hobbiton was located in the center of the Shire in the far eastern 
                part of the Westfarthing. It was the home of many illustrious Hobbits, 
                including Bilbo Baggins, Frodo Baggins, and Samwise Gamgee.
            """.trimIndent()
    )

    private val testPois = listOf(testPoi)

    private val poiRepository: PoiRepository = PoiRepositoryStub(testPoi, testPois)

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
            ), observer.observedValues
        )
    }

    @Test
    fun `load POIs success`() {
        val viewModel = MapViewModel(poiRepository)
        val observer = viewModel.getUiState().testObserver()

        viewModel.loadPois(0.0, 0.0)
        assertEquals(
            listOf(
                MapUiState.Loading,
                MapUiState.PoiReady(userPoi = testPoi, pois = testPois)
            ),
            observer.observedValues
        )
    }
}