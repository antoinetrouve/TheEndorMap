package com.antoinetrouve.theendormap.poi

class PoiRepositoryList: PoiRepository {
    override fun getUserPoi(latitude: Double, longitude: Double): Poi {
        return generateUserPoi(latitude, longitude)
    }

    override fun getPois(latitude: Double, longitude: Double): List<Poi> {
        return generatePois(latitude, longitude)
    }
}