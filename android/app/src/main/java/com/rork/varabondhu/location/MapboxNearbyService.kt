package com.rork.varabondhu.location

import com.mapbox.geojson.Point
import com.rork.varabondhu.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.math.roundToInt

/** Retrieves nearby Bangladesh map features from the Mapbox Streets tileset. */
object MapboxNearbyService {
    private val client: HttpClient = HttpClient(Android) {
        install(HttpTimeout)
    }
    private val json: Json = Json { ignoreUnknownKeys = true }

    suspend fun nearbyPlaces(point: Point): List<LocationPlace> = coroutineScope {
        val poiRequest = async {
            runCatching { queryLayer(point = point, layer = POI_LAYER) }
        }
        val transitRequest = async {
            runCatching { queryLayer(point = point, layer = TRANSIT_LAYER) }
        }
        val poiResult = poiRequest.await()
        val transitResult = transitRequest.await()
        if (poiResult.isFailure && transitResult.isFailure) {
            throw poiResult.exceptionOrNull() ?: error("Nearby map requests failed")
        }

        (transitResult.getOrDefault(emptyList()) + poiResult.getOrDefault(emptyList()))
            .sortedBy { result: Pair<LocationPlace, Double> -> result.second }
            .distinctBy { result: Pair<LocationPlace, Double> -> result.first.coordinateKey() }
            .take(MAX_VISIBLE_RESULTS)
            .map { result: Pair<LocationPlace, Double> -> result.first }
    }

    private suspend fun queryLayer(point: Point, layer: String): List<Pair<LocationPlace, Double>> {
        val response: HttpResponse = client.get(
            "$TILEQUERY_BASE_URL/${point.longitude()},${point.latitude()}.json"
        ) {
            parameter("radius", SEARCH_RADIUS_METERS)
            parameter("limit", RESULT_LIMIT_PER_LAYER)
            parameter("layers", layer)
            parameter("access_token", BuildConfig.MAPBOX_ACCESS_TOKEN)
            timeout {
                connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
                requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
                socketTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            }
        }
        if (!response.status.isSuccess()) {
            error("Nearby map request failed")
        }

        val features = json.parseToJsonElement(response.bodyAsText())
            .jsonObject["features"]
            ?.jsonArray
            .orEmpty()
        return features.mapNotNull { feature ->
            val featureObject = feature.jsonObject
            val properties = featureObject["properties"]?.jsonObject ?: return@mapNotNull null
            if (properties.stringValue("iso_3166_1") != BANGLADESH_COUNTRY_CODE) {
                return@mapNotNull null
            }
            val name = properties.stringValue("name")
                ?.takeIf(String::isNotBlank)
                ?: properties.stringValue("name_en")?.takeIf(String::isNotBlank)
                ?: return@mapNotNull null
            if (layer == POI_LAYER && !properties.isUsefulLandmark()) {
                return@mapNotNull null
            }
            val coordinates = featureObject["geometry"]
                ?.jsonObject
                ?.get("coordinates")
                ?.jsonArray
                ?: return@mapNotNull null
            val longitude = coordinates.getOrNull(0)?.jsonPrimitive?.doubleOrNull
                ?: return@mapNotNull null
            val latitude = coordinates.getOrNull(1)?.jsonPrimitive?.doubleOrNull
                ?: return@mapNotNull null
            if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
                return@mapNotNull null
            }
            val category = properties.stringValue("category_en")
                ?: properties.stringValue("type")
                ?: properties.stringValue("mode")
                ?: "Nearby landmark"
            val distance = properties["tilequery"]
                ?.jsonObject
                ?.get("distance")
                ?.jsonPrimitive
                ?.doubleOrNull
                ?: Double.MAX_VALUE
            LocationPlace(
                name = name,
                address = category,
                latitude = latitude,
                longitude = longitude
            ) to distance
        }
    }

    private fun JsonObject.stringValue(key: String): String? =
        get(key)?.jsonPrimitive?.contentOrNull

    private fun JsonObject.isUsefulLandmark(): Boolean {
        val type = stringValue("type")?.lowercase().orEmpty()
        val featureClass = stringValue("class")?.lowercase().orEmpty()
        return type !in EXCLUDED_POI_TYPES && featureClass != EXCLUDED_POI_CLASS
    }

    private fun LocationPlace.coordinateKey(): String =
        "${(latitude * COORDINATE_ROUNDING).roundToInt()}-${(longitude * COORDINATE_ROUNDING).roundToInt()}"

    private const val TILEQUERY_BASE_URL =
        "https://api.mapbox.com/v4/mapbox.mapbox-streets-v8/tilequery"
    private const val POI_LAYER = "poi_label"
    private const val TRANSIT_LAYER = "transit_stop_label"
    private const val BANGLADESH_COUNTRY_CODE = "BD"
    private const val SEARCH_RADIUS_METERS = 10_000
    private const val RESULT_LIMIT_PER_LAYER = 50
    private const val MAX_VISIBLE_RESULTS = 6
    private const val CONNECT_TIMEOUT_MILLIS = 4_000L
    private const val REQUEST_TIMEOUT_MILLIS = 8_000L
    private const val COORDINATE_ROUNDING = 10_000.0
    private const val EXCLUDED_POI_CLASS = "general"
    private val EXCLUDED_POI_TYPES: Set<String> = setOf("company", "yes")
}
