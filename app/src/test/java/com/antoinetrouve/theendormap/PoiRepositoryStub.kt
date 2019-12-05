package com.antoinetrouve.theendormap

import com.antoinetrouve.theendormap.poi.Poi
import com.antoinetrouve.theendormap.poi.PoiRepository

class PoiRepositoryStub(private val userPoi: Poi, private val pois: List<Poi>) : PoiRepository {
    override fun getUserPoi(latitude: Double, longitude: Double): Poi = userPoi
    override fun getPois(latitude: Double, longitude: Double): List<Poi> = pois
}