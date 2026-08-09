package com.rork.varabondhu.location

import com.mapbox.geojson.Point

/** A user-facing place backed by a precise Mapbox coordinate. */
data class LocationPlace(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
) {
    val point: Point
        get() = Point.fromLngLat(longitude, latitude)

    val displayName: String
        get() = name.ifBlank { address }
}

/** Identifies which end of the active route is being edited. */
enum class LocationTarget(val routeValue: String) {
    ORIGIN("origin"),
    DESTINATION("destination");

    companion object {
        fun fromRouteValue(value: String?): LocationTarget =
            entries.firstOrNull { it.routeValue == value } ?: ORIGIN
    }
}

/** Lightweight presentation model for a Mapbox autocomplete result. */
data class LocationSuggestion(
    val id: Int,
    val name: String,
    val address: String
)
