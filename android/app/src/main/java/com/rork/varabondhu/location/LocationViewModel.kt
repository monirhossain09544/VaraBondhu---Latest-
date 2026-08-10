package com.rork.varabondhu.location

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.BoundingBox
import com.mapbox.geojson.Point
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteOptions
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.common.IsoCountryCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState.asStateFlow()

    private var searchSelections: List<SearchSelection> = emptyList()
    private var searchJob: Job? = null
    private var lastNearbyLoadTimeMillis: Long = 0L

    fun beginSelection(target: LocationTarget) {
        searchJob?.cancel()
        searchSelections = emptyList()
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
        searchSelections = emptyList()
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
            delay(SEARCH_DEBOUNCE_MILLIS)
            val trimmedQuery: String = query.trim()
            val curatedPlaces: List<LocationPlace> = CuratedBangladeshPlaceSearch.search(trimmedQuery)
            if (curatedPlaces.isNotEmpty()) {
                searchSelections = curatedPlaces.map(SearchSelection::Direct)
                _uiState.update { state: LocationUiState ->
                    if (state.query.trim() != trimmedQuery) {
                        state
                    } else {
                        state.copy(
                            suggestions = searchSelections.mapIndexed { index: Int, selection: SearchSelection ->
                                LocationSuggestion(
                                    id = index,
                                    name = selection.name,
                                    address = selection.address
                                )
                            },
                            errorMessage = null
                        )
                    }
                }
            }
            val proximity: Point = _uiState.value.currentDevicePlace?.point
                ?.takeIf(::isWithinBangladeshSearchBounds)
                ?: BANGLADESH_SEARCH_CENTER
            val search = placeAutocomplete
            val mapboxRequest = search?.let { activeSearch: PlaceAutocomplete ->
                async {
                    runCatching {
                        activeSearch.suggestions(
                            query = trimmedQuery,
                            region = BANGLADESH_BOUNDS,
                            proximity = proximity,
                            options = BANGLADESH_AUTOCOMPLETE_OPTIONS
                        )
                    }
                }
            }
            val fallbackRequest = async {
                runCatching {
                    BangladeshPlaceSearchService.search(
                        query = trimmedQuery,
                        proximity = proximity
                    )
                }
            }
            val mapboxResult = mapboxRequest?.await()
            val mapboxResponse = mapboxResult?.getOrNull()
            val fallbackResult = fallbackRequest.await()
            val mapboxSuggestions: List<PlaceAutocompleteSuggestion> =
                mapboxResponse?.value.orEmpty()
            val fallbackPlaces: List<LocationPlace> = fallbackResult.getOrDefault(emptyList())

            searchSelections = mergeSearchResults(
                mapboxSuggestions = mapboxSuggestions,
                fallbackPlaces = curatedPlaces + fallbackPlaces
            )
            val didEveryProviderFail: Boolean =
                (search == null || mapboxResult?.isFailure == true || mapboxResponse?.isError == true) &&
                    fallbackResult.isFailure
            _uiState.update { state: LocationUiState ->
                state.copy(
                    suggestions = searchSelections.mapIndexed { index: Int, selection: SearchSelection ->
                        LocationSuggestion(
                            id = index,
                            name = selection.name,
                            address = selection.address
                        )
                    },
                    isSearching = false,
                    errorMessage = when {
                        searchSelections.isNotEmpty() -> null
                        didEveryProviderFail -> "স্থান খোঁজা যাচ্ছে না। আবার চেষ্টা করুন।"
                        else -> "কোনো স্থান পাওয়া যায়নি"
                    }
                )
            }
        }
    }

    fun selectSuggestion(suggestionId: Int) {
        when (val selection: SearchSelection = searchSelections.getOrNull(suggestionId) ?: return) {
            is SearchSelection.Direct -> commitPlace(selection.place)
            is SearchSelection.Mapbox -> selectMapboxSuggestion(selection.suggestion)
        }
    }

    private fun selectMapboxSuggestion(suggestion: PlaceAutocompleteSuggestion) {
        val search = placeAutocomplete
        if (search == null) {
            _uiState.update { state: LocationUiState ->
                state.copy(isSearching = false, errorMessage = SEARCH_UNAVAILABLE)
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { state: LocationUiState -> state.copy(isSearching = true, errorMessage = null) }
            val response = search.select(suggestion)
            val result = response.value
            if (response.isValue && result != null && isWithinBangladeshSearchBounds(result.coordinate)) {
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
                    state.copy(
                        isSearching = false,
                        errorMessage = "শুধু বাংলাদেশের ভেতরের লোকেশন নির্বাচন করুন।"
                    )
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
            val places = runCatching { MapboxNearbyService.nearbyPlaces(point) }
                .getOrElse {
                    _uiState.update { state: LocationUiState ->
                        state.copy(
                            isLoadingNearby = false,
                            errorMessage = "আশেপাশের স্থান লোড করা যাচ্ছে না।"
                        )
                    }
                    return@launch
                }
            lastNearbyLoadTimeMillis = System.currentTimeMillis()
            _uiState.update { state: LocationUiState ->
                state.copy(
                    nearbyPlaces = places,
                    isLoadingNearby = false,
                    errorMessage = if (places.isEmpty()) {
                        "আশেপাশে কোনো পরিচিত স্থান পাওয়া যায়নি।"
                    } else {
                        null
                    }
                )
            }
        }
    }

    fun useCurrentLocation(context: Context) {
        _uiState.value.currentDevicePlace
            ?.takeIf { place: LocationPlace -> isWithinBangladeshSearchBounds(place.point) }
            ?.let { place: LocationPlace ->
                commitPlace(place)
                return
            }

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

            val point: Point = Point.fromLngLat(location.longitude, location.latitude)
            if (!isWithinBangladeshSearchBounds(point)) {
                _uiState.update { state: LocationUiState ->
                    state.copy(
                        isResolvingPoint = false,
                        errorMessage = "শুধু বাংলাদেশের ভেতরের লোকেশন নির্বাচন করুন।"
                    )
                }
                return@launch
            }

            val fallbackPlace = LocationPlace(
                name = "আপনার বর্তমান অবস্থান",
                address = "আপনার বর্তমান অবস্থান",
                latitude = point.latitude(),
                longitude = point.longitude()
            )
            val currentPlace: LocationPlace = runCatching {
                resolvePlace(point = point, fallbackName = fallbackPlace.name)
            }.getOrDefault(fallbackPlace)
            _uiState.update { state: LocationUiState -> state.copy(currentDevicePlace = currentPlace) }
            commitPlace(currentPlace)
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
        if (!isWithinBangladeshSearchBounds(point)) {
            _uiState.update { state: LocationUiState ->
                state.copy(
                    isResolvingPoint = false,
                    errorMessage = "শুধু বাংলাদেশের ভেতরের লোকেশন নির্বাচন করুন।"
                )
            }
            return
        }
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
        searchSelections = emptyList()
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

    private fun mergeSearchResults(
        mapboxSuggestions: List<PlaceAutocompleteSuggestion>,
        fallbackPlaces: List<LocationPlace>
    ): List<SearchSelection> =
        (fallbackPlaces.map(SearchSelection::Direct) +
            mapboxSuggestions.map(SearchSelection::Mapbox))
            .distinctBy { selection: SearchSelection ->
                "${selection.name.normalizedSearchText()}|${selection.address.normalizedSearchText()}"
            }
            .take(MAX_SEARCH_RESULTS)

    private fun String.normalizedSearchText(): String =
        lowercase().filter { character: Char -> character.isLetterOrDigit() }

    private fun isWithinBangladeshSearchBounds(point: Point): Boolean =
        point.longitude() in BANGLADESH_WEST..BANGLADESH_EAST &&
            point.latitude() in BANGLADESH_SOUTH..BANGLADESH_NORTH

    private sealed interface SearchSelection {
        val name: String
        val address: String

        data class Mapbox(
            val suggestion: PlaceAutocompleteSuggestion
        ) : SearchSelection {
            override val name: String = suggestion.name
            override val address: String = suggestion.formattedAddress.orEmpty()
        }

        data class Direct(
            val place: LocationPlace
        ) : SearchSelection {
            override val name: String = place.name
            override val address: String = place.address
        }
    }

    private companion object {
        const val SEARCH_UNAVAILABLE = "স্থান খোঁজার সেবা এখন পাওয়া যাচ্ছে না।"
        const val SEARCH_DEBOUNCE_MILLIS = 550L
        const val MAX_SEARCH_RESULTS = 10
        const val NEARBY_CACHE_MAX_AGE_MILLIS = 2 * 60 * 1_000L
        const val BANGLADESH_WEST = 88.0
        const val BANGLADESH_SOUTH = 20.5
        const val BANGLADESH_EAST = 92.7
        const val BANGLADESH_NORTH = 26.7
        val BANGLADESH_SEARCH_CENTER: Point = Point.fromLngLat(90.3563, 23.6850)
        val BANGLADESH_BOUNDS: BoundingBox = BoundingBox.fromLngLats(
            BANGLADESH_WEST,
            BANGLADESH_SOUTH,
            BANGLADESH_EAST,
            BANGLADESH_NORTH
        )
        val BANGLADESH_AUTOCOMPLETE_OPTIONS: PlaceAutocompleteOptions =
            PlaceAutocompleteOptions(
                limit = 10,
                countries = listOf(IsoCountryCode("BD"))
            )
    }
}
