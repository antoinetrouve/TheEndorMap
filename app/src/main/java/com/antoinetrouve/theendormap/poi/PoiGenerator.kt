package com.antoinetrouve.theendormap.poi

import android.graphics.Color
import com.antoinetrouve.theendormap.R

const val GREY_HAVENS = "Grey Havens"
const val HOBBITON = "Hobbiton"
const val ISENGARD = "Isengard"
const val MORIA_GATES = "Moria Gates"
const val LORIEN = "Lorien"
const val GONDOR = "Gondor"
const val MOUNT_DOOM = "Mount Doom"
const val FRODO_BAGGINS = "Frodo Baggins"

/**
 * Initial position on map
 * @param deltaX value absciss position
 * @param deltaY value ordinate position
 */
private data class Loc(val deltaX: Double,
                       val deltaY: Double)

private val locations = mapOf(
    GREY_HAVENS to Loc(
        -13.1,
        4.5
    ),
    HOBBITON to Loc(-10.8, 4.5),
    ISENGARD to Loc(-5.8, 1.5),
    MORIA_GATES to Loc(
        -5.1,
        4.5
    ),
    LORIEN to Loc(-3.6, 4.0),
    GONDOR to Loc(-0.5, -1.8),
    MOUNT_DOOM to Loc(1.5, -1.0)
)

fun generateUserPoi(latitude: Double, longitude: Double): Poi {
    return Poi(
        title = FRODO_BAGGINS,
        latitude = latitude,
        longitude = longitude,
        imageId = R.drawable.frodobaggins,
        iconId = R.drawable.marker_frodo,
        detailUrl = "http://lotr.wikia.com/wiki/Frodo_Baggins",
        description = """
            Frodo Baggins, son of Drogo Baggins, was a Hobbit of the Shire during the Third Age. 
            He was, and still is, Tolkien's most renowned character for his leading role in the 
            Quest of the Ring, in which he bore the One Ring to Mount Doom, where it was destroyed
        """.trimIndent()
    )
}

/**
 * Generate position according to user position
 */
fun generatePois(latitude: Double, longitude: Double) : List<Poi> {
    return listOf(
        Poi(
            title = GREY_HAVENS,
            latitude = distToLat(
                GREY_HAVENS,
                latitude
            ),
            longitude = distToLong(
                GREY_HAVENS,
                longitude
            ),
            imageId = R.drawable.greyhavens,
            iconColor = Color.BLUE,
            detailUrl = "http://lotr.wikia.com/wiki/Grey_Havens",
            description = """
               Because of its cultural and spiritual importance to the Elves, 
               the Grey Havens in time became the primary Elven settlement 
               west of the Misty Mountains prior to the establishment of Eregion and, 
               later, Rivendell.
            """.trimIndent()
        ),
        Poi(
            title = HOBBITON,
            latitude = distToLat(
                HOBBITON,
                latitude
            ),
            longitude = distToLong(
                HOBBITON,
                longitude
            ),
            imageId = R.drawable.hobbiton,
            iconColor = Color.GREEN,
            detailUrl = "http://lotr.wikia.com/wiki/Hobbiton",
            description = """
                Hobbiton was located in the center of the Shire in the far eastern 
                part of the Westfarthing. It was the home of many illustrious Hobbits, 
                including Bilbo Baggins, Frodo Baggins, and Samwise Gamgee.
            """.trimIndent()
        ),
        Poi(
            title = ISENGARD,
            latitude = distToLat(
                ISENGARD,
                latitude
            ),
            longitude = distToLong(
                ISENGARD,
                longitude
            ),
            imageId = R.drawable.isengard,
            iconColor = Color.RED,
            detailUrl = "http://lotr.wikia.com/wiki/Isengard",
            description = """
                Isengard, also known as Angrenost ('Iron Fortress') in Sindarin, 
                was one of the three Fortresses of Gondor, 
                and held within it one of the realm's Palantiri.
            """.trimIndent()
        ),
        Poi(
            title = MORIA_GATES,
            latitude = distToLat(
                MORIA_GATES,
                latitude
            ),
            longitude = distToLong(
                MORIA_GATES,
                longitude
            ),
            imageId = R.drawable.moriagates,
            iconColor = Color.YELLOW,
            detailUrl = "http://lotr.wikia.com/wiki/Khazad-d%C3%BBm",
            description = """
                Khazad-dûm, also commonly known as Moria or the Dwarrowdelf,
                 was an underground kingdom beneath the Misty Mountains.
            """.trimIndent()
        ),
        Poi(
            title = LORIEN,
            latitude = distToLat(
                LORIEN,
                latitude
            ),
            longitude = distToLong(
                LORIEN,
                longitude
            ),
            imageId = R.drawable.lorien,
            iconColor = Color.GREEN,
            detailUrl = "http://lotr.wikia.com/wiki/Lothl%C3%B3rien",
            description = """
                Lothlórien was both a forest and elven realm located next to 
                the lower Misty Mountains. It was first settled by Nandorin elves, 
                but later enriched by Ñoldor and Sindar, under Celeborn of Doriath and Galadriel, 
                daughter of Finarfin
            """.trimIndent()
        ),
        Poi(
            title = GONDOR,
            latitude = distToLat(
                GONDOR,
                latitude
            ),
            longitude = distToLong(
                GONDOR,
                longitude
            ),
            imageId = R.drawable.gondor,
            iconColor = Color.BLUE,
            detailUrl = "http://lotr.wikia.com/wiki/Gondor",
            description = """
                Gondor was the prominent kingdom of Men in Middle-earth, 
                bordered by Rohan to the north, Harad to the south, 
                the cape of Andrast and the Sea to the west, and Mordor to the east.
            """.trimIndent()
        ),
        Poi(
            title = MOUNT_DOOM,
            latitude = distToLat(
                MOUNT_DOOM,
                latitude
            ),
            longitude = distToLong(
                MOUNT_DOOM,
                longitude
            ),
            imageId = R.drawable.mountdoom,
            iconColor = Color.RED,
            detailUrl = "http://lotr.wikia.com/wiki/Mount_Doom",
            description = """
                Mount Doom, also known as Orodruin and Amon Amarth, was a volcano in Mordor 
                where the One Ring was forged and finally destroyed. 
                It was the ultimate destination for Frodo's Quest of the Ring.
            """.trimIndent()
        )
    )
}

// Formula for the conversion:
// 1° = 111km
// 1km = 0,405405405405405°
// Multiplier : 10 to have bigger distances
// Original Distance * Multiplier * degrees
// With Grey Havens : -13.1 * 10 * 0,405405405405405° = -0.630630630630631

private const val MULTIPLIER = 10.0
private const val ONE_KM_IN_DEG = 0.009009009009009

private fun distToLat(name: String, coordinate: Double) : Double {
    return coordinate + locations.getValue(name).deltaY * MULTIPLIER * ONE_KM_IN_DEG
}

private fun distToLong(name: String, coordinate: Double) : Double {
    return coordinate + locations.getValue(name).deltaX * MULTIPLIER * ONE_KM_IN_DEG
}