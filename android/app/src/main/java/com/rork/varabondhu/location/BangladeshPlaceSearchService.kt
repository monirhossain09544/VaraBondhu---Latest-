package com.rork.varabondhu.location

import com.mapbox.geojson.Point
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Locale

/** Searches OpenStreetMap-backed Bangladesh places when Mapbox has no local POI coverage. */
object BangladeshPlaceSearchService {
    private val client: HttpClient = HttpClient(Android) {
        install(HttpTimeout)
    }
    private val json: Json = Json { ignoreUnknownKeys = true }
    private val cache: LinkedHashMap<String, CacheEntry> = LinkedHashMap()

    suspend fun search(query: String, proximity: Point): List<LocationPlace> {
        val normalizedQuery = query.trim().lowercase(Locale.ROOT)
        if (normalizedQuery.length < MINIMUM_QUERY_LENGTH) return emptyList()

        cachedPlaces(normalizedQuery)?.let { places: List<LocationPlace> -> return places }

        val response: HttpResponse = client.get(PHOTON_SEARCH_URL) {
            parameter("q", query.trim())
            parameter("limit", REQUEST_RESULT_LIMIT)
            parameter("lat", proximity.latitude())
            parameter("lon", proximity.longitude())
            parameter("bbox", BANGLADESH_BOUNDING_BOX)
            header(HttpHeaders.UserAgent, USER_AGENT)
            timeout {
                connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
                requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
                socketTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            }
        }
        if (!response.status.isSuccess()) error("Bangladesh place search failed")

        val places: List<LocationPlace> = json.parseToJsonElement(response.bodyAsText())
            .jsonObject["features"]
            ?.jsonArray
            .orEmpty()
            .mapNotNull { feature -> parseFeature(feature.jsonObject) }
            .distinctBy { place: LocationPlace -> place.coordinateKey() }
            .take(MAX_RESULTS)
        cachePlaces(normalizedQuery, places)
        return places
    }

    private fun parseFeature(feature: JsonObject): LocationPlace? {
        val properties: JsonObject = feature["properties"]?.jsonObject ?: return null
        if (properties.stringValue("countrycode")?.uppercase(Locale.ROOT) != BANGLADESH_COUNTRY_CODE) {
            return null
        }
        val name: String = properties.stringValue("name")?.takeIf(String::isNotBlank) ?: return null
        val coordinates = feature["geometry"]?.jsonObject?.get("coordinates")?.jsonArray ?: return null
        val longitude: Double = coordinates.getOrNull(0)?.jsonPrimitive?.doubleOrNull ?: return null
        val latitude: Double = coordinates.getOrNull(1)?.jsonPrimitive?.doubleOrNull ?: return null
        if (longitude !in BANGLADESH_WEST..BANGLADESH_EAST ||
            latitude !in BANGLADESH_SOUTH..BANGLADESH_NORTH
        ) {
            return null
        }

        val addressParts: List<String> = listOfNotNull(
            properties.stringValue("street"),
            properties.stringValue("locality"),
            properties.stringValue("district"),
            properties.stringValue("city"),
            properties.stringValue("county"),
            properties.stringValue("state"),
            properties.stringValue("postcode"),
            properties.stringValue("country")
        ).map(String::trim)
            .filter { part: String -> part.isNotBlank() && !part.equals(name, ignoreCase = true) }
            .distinctBy { part: String -> part.lowercase(Locale.ROOT) }

        return LocationPlace(
            name = name,
            address = addressParts.joinToString(", ").ifBlank { BANGLADESH_DISPLAY_NAME },
            latitude = latitude,
            longitude = longitude
        )
    }

    private fun cachedPlaces(query: String): List<LocationPlace>? = synchronized(cache) {
        val entry: CacheEntry = cache[query] ?: return@synchronized null
        if (System.currentTimeMillis() - entry.savedAtMillis > CACHE_MAX_AGE_MILLIS) {
            cache.remove(query)
            null
        } else {
            entry.places
        }
    }

    private fun cachePlaces(query: String, places: List<LocationPlace>) = synchronized(cache) {
        cache[query] = CacheEntry(places = places, savedAtMillis = System.currentTimeMillis())
        while (cache.size > MAX_CACHE_ENTRIES) {
            cache.remove(cache.keys.first())
        }
    }

    private fun JsonObject.stringValue(key: String): String? =
        get(key)?.jsonPrimitive?.contentOrNull

    private fun LocationPlace.coordinateKey(): String =
        "${"%.5f".format(Locale.ROOT, latitude)},${"%.5f".format(Locale.ROOT, longitude)}"

    private data class CacheEntry(
        val places: List<LocationPlace>,
        val savedAtMillis: Long
    )

    private const val PHOTON_SEARCH_URL = "https://photon.komoot.io/api/"
    private const val USER_AGENT = "VaraBondhu/1.0 Android Bangladesh location search"
    private const val BANGLADESH_COUNTRY_CODE = "BD"
    private const val BANGLADESH_DISPLAY_NAME = "Bangladesh"
    private const val BANGLADESH_BOUNDING_BOX = "88.0,20.5,92.7,26.7"
    private const val BANGLADESH_WEST = 88.0
    private const val BANGLADESH_SOUTH = 20.5
    private const val BANGLADESH_EAST = 92.7
    private const val BANGLADESH_NORTH = 26.7
    private const val MINIMUM_QUERY_LENGTH = 3
    private const val REQUEST_RESULT_LIMIT = 20
    private const val MAX_RESULTS = 10
    private const val MAX_CACHE_ENTRIES = 50
    private const val CACHE_MAX_AGE_MILLIS = 15 * 60 * 1_000L
    private const val CONNECT_TIMEOUT_MILLIS = 4_000L
    private const val REQUEST_TIMEOUT_MILLIS = 8_000L
}
