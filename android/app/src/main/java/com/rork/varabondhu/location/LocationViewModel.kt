package com.rork.varabondhu.location

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.search.ApiType
import com.mapbox.search.CategorySearchOptions
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.common.IsoCountryCode
import com.mapbox.search.result.SearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Shared location state for route search, fare contribution, and fare results. */
data class LocationUiState(
    val origin: LocationPlace = LocationDefaults.mirpur,
    val destination: LocationPlace = LocationDefaults.farmgate,
    val activeTarget: LocationTarget = LocationTarget.ORIGIN,
    val query: String = "",
    val suggestions: List<LocationSuggestion> = emptyList(),
    val nearbyPlaces: List<LocationPlace> = emptyList(),
    val currentDevicePlace: LocationPlace? = null,
    val pendingMapPlace: LocationPlace? = null,
    val isSearching: Boolean = false,
    val isLoadingNearby: Boolean = false,
    val isResolvingPoint: Boolean = false,
    val errorMessage: String? = null,
    val selectionRevision: Int = 0
)

/** Known initial places keep the existing prototype useful before the first edit. */
object LocationDefaults {
    val mirpur: LocationPlace = LocationPlace(
        name = "মিরপুর ১০",
        address = "মিরপুর ১০, ঢাকা",
        latitude = 23.8067,
        longitude = 90.3686
    )
    val farmgate: LocationPlace = LocationPlace(
        name = "ফার্মগেট",
        address = "ফার্মগেট, ঢাকা",
        latitude = 23.7582,
        longitude = 90.3890
    )
}

class LocationViewModel : ViewModel() {
    /**
     * Created lazily so a Search SDK initialization failure surfaces as an in-screen
     * message instead of crashing the app during navigation setup.
     */
    private val placeAutocomplete: PlaceAutocomplete? by lazy {
        runCatching { PlaceAutocomplete.create() }.getOrNull()
    }
    private val nearbySearchEngine: SearchEngine? by lazy {
        runCatching {
            SearchEngine.createSearchEngineWithBuiltInDataProviders(
                apiType = ApiType.SEARCH_BOX,
                settings = SearchEngineSettings()
            )
        }.getOrNull()
    }
    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    private var rawSuggestions: List<PlaceAutocompleteSuggestion> = emptyList()
    private var searchJob: Job? = null
    private var lastNearbyLoadTimeMillis: Long = 0L

    fun beginSelection(target: LocationTarget) {
        searchJob?.cancel()
        rawSuggestions = emptyList()
        _uiState.update { state: LocationUiState ->
            val selected = if (target == LocationTarget.ORIGIN) state.origin else state.destination
            state.copy(
                activeTarget = target,
                query = "",
                suggestions = emptyList(),
                pendingMapPlace = selected,
                isSearching = false,
                isResolvingPoint = false,
                errorMessage = null
            )
        }
    }

    fun updateQuery(query: String) {
        searchJob?.cancel()
        rawSuggestions = emptyList()
        _uiState.update { state: LocationUiState ->
            state.copy(
                query = query,
                suggestions = emptyList(),
                isSearching = query.trim().length >= 2,
                errorMessage = null
            )
        }
        if (query.trim().length < 2) return

        searchJob = viewModelScope.launch {
            delay(350)
            val search = placeAutocomplete
            if (search == null) {
                _uiState.update { state: LocationUiState ->
                    state.copy(isSearching = false, errorMessage = SEARCH_UNAVAILABLE)
                }
                return@launch
            }
            val response = search.suggestions(
                query = query.trim(),
                proximity = _uiState.value.currentDevicePlace?.point
                    ?: Point.fromLngLat(90.4125, 23.8103)
            )
            if (response.isValue) {
                rawSuggestions = response.value.orEmpty()
                _uiState.update { state: LocationUiState ->
                    state.copy(
                        suggestions = rawSuggestions.mapIndexed { index, suggestion ->
                            LocationSuggestion(
                                id = index,
                                name = suggestion.name,
                                address = suggestion.formattedAddress.orEmpty()
                            )
                        },
                        isSearching = false,
                        errorMessage = if (rawSuggestions.isEmpty()) "কোনো স্থান পাওয়া যায়নি" else null
                    )
                }
            } else {
                _uiState.update { state: LocationUiState ->
                    state.copy(
                        isSearching = false,
                        errorMessage = "স্থান খোঁজা যাচ্ছে না। আবার চেষ্টা করুন।"
                    )
                }
            }
        }
    }

    fun selectSuggestion(suggestionId: Int) {
        val suggestion = rawSuggestions.getOrNull(suggestionId) ?: return
        val search = placeAutocomplete ?: return
        viewModelScope.launch {
            _uiState.update { state: LocationUiState -> state.copy(isSearching = true, errorMessage = null) }
            val response = search.select(suggestion)
            val result = response.value
            if (response.isValue && result != null) {
                commitPlace(
                    LocationPlace(
                        name = result.name,
                        address = result.address?.formattedAddress ?: suggestion.formattedAddress.orEmpty(),
                        latitude = result.coordinate.latitude(),
                        longitude = result.coordinate.longitude()
                    )
                )
            } else {
                _uiState.update { state: LocationUiState ->
                    state.copy(isSearching = false, errorMessage = "স্থানটি নির্বাচন করা যায়নি।")
                }
            }
        }
    }

    fun previewMapPoint(point: Point) {
        resolvePoint(point = point, shouldCommit = false)
    }

    fun loadNearbyPlaces(context: Context) {
        val currentState = _uiState.value
        if (currentState.isLoadingNearby) return
        val hasRecentNearbyResults = currentState.currentDevicePlace != null &&
            currentState.nearbyPlaces.isNotEmpty() &&
            System.currentTimeMillis() - lastNearbyLoadTimeMillis <= NEARBY_CACHE_MAX_AGE_MILLIS
        if (hasRecentNearbyResults) return
        viewModelScope.launch {
            _uiState.update { state: LocationUiState ->
                state.copy(
                    nearbyPlaces = emptyList(),
                    isLoadingNearby = true,
                    errorMessage = null
                )
            }
            val location = DeviceLocationProvider.currentLocation(context)
            if (location == null) {
                _uiState.update { state: LocationUiState ->
                    state.copy(
                        isLoadingNearby = false,
                        errorMessage = "আশেপাশের স্থান দেখতে লোকেশন চালু করুন।"
                    )
                }
                return@launch
            }
            val point = Point.fromLngLat(location.longitude, location.latitude)
            val provisionalCurrentPlace = LocationPlace(
                name = "আপনার বর্তমান অবস্থান",
                address = "আপনার বর্তমান অবস্থান",
                latitude = point.latitude(),
                longitude = point.longitude()
            )
            _uiState.update { state: LocationUiState ->
                state.copy(currentDevicePlace = provisionalCurrentPlace)
            }
            viewModelScope.launch {
                val resolvedPlace = resolvePlace(point, provisionalCurrentPlace.name)
                _uiState.update { state: LocationUiState ->
                    val activePlace = state.currentDevicePlace
                    if (activePlace?.latitude == point.latitude() &&
                        activePlace.longitude == point.longitude()
                    ) {
                        state.copy(currentDevicePlace = resolvedPlace)
                    } else {
                        state
                    }
                }
            }
            val engine = nearbySearchEngine
            if (engine == null) {
                _uiState.update { state: LocationUiState ->
                    state.copy(
                        isLoadingNearby = false,
                        errorMessage = SEARCH_UNAVAILABLE
                    )
                }
                return@launch
            }
            engine.search(
                listOf("bus_station", "railway_station", "market", "hospital", "landmark"),
                CategorySearchOptions(
                    proximity = point,
                    origin = point,
                    countries = listOf(IsoCountryCode("BD")),
                    limit = 12,
                    ensureResultsPerCategory = false
                ),
                object : SearchCallback {
                    override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
                        val places = results
                            .mapNotNull { result: SearchResult ->
                                val distanceMeters = distanceMeters(
                                    from = point,
                                    to = result.coordinate
                                )
                                if (distanceMeters > MAX_NEARBY_DISTANCE_METERS) {
                                    null
                                } else {
                                    NearbyPlace(
                                        place = LocationPlace(
                                            name = result.name,
                                            address = (result.fullAddress ?: result.name)
                                                .ifBlank { result.name },
                                            latitude = result.coordinate.latitude(),
                                            longitude = result.coordinate.longitude()
                                        ),
                                        distanceMeters = distanceMeters
                                    )
                                }
                            }
                            .sortedBy(NearbyPlace::distanceMeters)
                            .distinctBy { nearby: NearbyPlace ->
                                nearby.place.name to nearby.place.address
                            }
                            .take(6)
                            .map(NearbyPlace::place)
                        lastNearbyLoadTimeMillis = System.currentTimeMillis()
                        _uiState.update { state: LocationUiState ->
                            state.copy(
                                nearbyPlaces = places,
                                isLoadingNearby = false,
                                errorMessage = if (places.isEmpty()) "আশেপাশে কোনো পরিচিত স্থান পাওয়া যায়নি।" else null
                            )
                        }
                    }

                    override fun onError(e: Exception) {
                        _uiState.update { state: LocationUiState ->
                            state.copy(
                                isLoadingNearby = false,
                                errorMessage = "আশেপাশের স্থান লোড করা যাচ্ছে না।"
                            )
                        }
                    }
                }
            )
        }
    }

    fun useCurrentLocation(context: Context) {
        viewModelScope.launch {
            _uiState.update { state: LocationUiState ->
                state.copy(isResolvingPoint = true, errorMessage = null)
            }
            val location = DeviceLocationProvider.currentLocation(context)
            if (location == null) {
                _uiState.update { state: LocationUiState ->
                    state.copy(
                        isResolvingPoint = false,
                        errorMessage = "বর্তমান অবস্থান পাওয়া যায়নি। লোকেশন চালু করে আবার চেষ্টা করুন।"
                    )
                }
                return@launch
            }
            resolvePoint(
                point = Point.fromLngLat(location.longitude, location.latitude),
                shouldCommit = true
            )
        }
    }

    fun selectPlace(place: LocationPlace) {
        commitPlace(place)
    }

    fun confirmPendingPlace() {
        _uiState.value.pendingMapPlace?.let(::commitPlace)
    }

    fun swapRoute() {
        _uiState.update { state: LocationUiState ->
            state.copy(origin = state.destination, destination = state.origin)
        }
    }

    fun clearMessage() {
        _uiState.update { state: LocationUiState -> state.copy(errorMessage = null) }
    }

    private suspend fun resolvePlace(point: Point, fallbackName: String): LocationPlace {
        val search = placeAutocomplete
        val response = search?.reverse(point)
        val suggestion = response?.value?.firstOrNull()
        return LocationPlace(
            name = suggestion?.name?.takeIf(String::isNotBlank) ?: fallbackName,
            address = suggestion?.formattedAddress?.takeIf(String::isNotBlank) ?: fallbackName,
            latitude = point.latitude(),
            longitude = point.longitude()
        )
    }

    private fun resolvePoint(point: Point, shouldCommit: Boolean) {
        viewModelScope.launch {
            _uiState.update { state: LocationUiState ->
                state.copy(isResolvingPoint = true, suggestions = emptyList(), errorMessage = null)
            }
            val search = placeAutocomplete
            if (search == null) {
                val coordinatePlace = LocationPlace(
                    name = "ম্যাপে নির্বাচিত স্থান",
                    address = "ম্যাপে নির্বাচিত স্থান",
                    latitude = point.latitude(),
                    longitude = point.longitude()
                )
                if (shouldCommit) {
                    commitPlace(coordinatePlace)
                } else {
                    _uiState.update { state: LocationUiState ->
                        state.copy(
                            pendingMapPlace = coordinatePlace,
                            isResolvingPoint = false,
                            errorMessage = SEARCH_UNAVAILABLE
                        )
                    }
                }
                return@launch
            }
            val response = search.reverse(point)
            val suggestion = response.value?.firstOrNull()
            val place = LocationPlace(
                name = suggestion?.name?.takeIf(String::isNotBlank)
                    ?: "ম্যাপে নির্বাচিত স্থান",
                address = suggestion?.formattedAddress?.takeIf(String::isNotBlank)
                    ?: "ম্যাপে নির্বাচিত স্থান",
                latitude = point.latitude(),
                longitude = point.longitude()
            )
            if (shouldCommit) {
                commitPlace(place)
            } else {
                _uiState.update { state: LocationUiState ->
                    state.copy(
                        pendingMapPlace = place,
                        query = place.displayName,
                        isResolvingPoint = false,
                        errorMessage = if (response.isError || suggestion == null) {
                            "ঠিকানা পাওয়া যায়নি—পিনের অবস্থান ব্যবহার হবে।"
                        } else {
                            null
                        }
                    )
                }
            }
        }
    }

    private fun commitPlace(place: LocationPlace) {
        rawSuggestions = emptyList()
        _uiState.update { state: LocationUiState ->
            if (state.activeTarget == LocationTarget.ORIGIN) {
                state.copy(
                    origin = place,
                    pendingMapPlace = place,
                    suggestions = emptyList(),
                    isSearching = false,
                    isResolvingPoint = false,
                    selectionRevision = state.selectionRevision + 1
                )
            } else {
                state.copy(
                    destination = place,
                    pendingMapPlace = place,
                    suggestions = emptyList(),
                    isSearching = false,
                    isResolvingPoint = false,
                    selectionRevision = state.selectionRevision + 1
                )
            }
        }
    }

    private fun distanceMeters(from: Point, to: Point): Float {
        val result = FloatArray(1)
        Location.distanceBetween(
            from.latitude(),
            from.longitude(),
            to.latitude(),
            to.longitude(),
            result
        )
        return result.first()
    }

    private data class NearbyPlace(
        val place: LocationPlace,
        val distanceMeters: Float
    )

    private companion object {
        const val SEARCH_UNAVAILABLE = "স্থান খোঁজার সেবা এখন পাওয়া যাচ্ছে না।"
        const val MAX_NEARBY_DISTANCE_METERS = 25_000f
        const val NEARBY_CACHE_MAX_AGE_MILLIS = 2 * 60 * 1_000L
    }
}
