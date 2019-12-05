package com.antoinetrouve.theendormap

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


/**
 * Allow to observe a generic object
 */
class TestObserver<T> : Observer<T> {

    val observedValues = mutableListOf<T?>()

    override fun onChanged(t: T) {
        observedValues.add(t)
    }
}

/**
 * Call on LiveData allowing observe
 */
fun <T> LiveData<T>.testObserver() = TestObserver<T>().apply {
    observeForever(this)
}