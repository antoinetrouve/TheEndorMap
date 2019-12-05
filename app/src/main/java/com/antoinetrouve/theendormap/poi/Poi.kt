package com.antoinetrouve.theendormap.poi

/**
 * iconId and iconColor are mutually exclusive
 * If the iconId is defined, iconColor should be left to the default value.
 */
data class Poi(val title: String,
               var latitude: Double,
               var longitude: Double,
               val imageId: Int = 0,
               val iconId: Int = 0,
               val iconColor: Int = 0,
               val description: String = "",
               val detailUrl: String = "")