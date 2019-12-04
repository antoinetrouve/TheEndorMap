package com.antoinetrouve.theendormap

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.antoinetrouve.theendormap.map.MapUiState
import com.antoinetrouve.theendormap.map.MapViewModel
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

class MapViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule() // useful to test LiveData

    @Test
    fun loadPoiTriggersLoadingTest() {
        val viewModel = MapViewModel()
        val observer: TestObserver<MapUiState> = viewModel.getUiState().testObserver()
        viewModel.loadPois(0.0, 0.0)

        Assert.assertEquals(MapUiState.Loading, observer.observedValues[0])
    }
}