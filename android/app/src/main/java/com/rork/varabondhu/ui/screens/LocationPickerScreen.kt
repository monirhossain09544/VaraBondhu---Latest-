package com.rork.varabondhu.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.GpsFixed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import com.rork.varabondhu.ui.localization.LocalizedText as Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.rork.varabondhu.location.LocationPlace
import com.rork.varabondhu.location.LocationSuggestion
import com.rork.varabondhu.location.LocationTarget
import com.rork.varabondhu.location.LocationUiState
import com.rork.varabondhu.ui.theme.BanglaFamily
import com.rork.varabondhu.ui.theme.BrandGreen
import com.rork.varabondhu.ui.theme.CardWhite
import com.rork.varabondhu.ui.theme.DangerRed
import com.rork.varabondhu.ui.theme.FieldBorder
import com.rork.varabondhu.ui.theme.FieldPlaceholder
import com.rork.varabondhu.ui.theme.Ink
import com.rork.varabondhu.ui.theme.InkMuted
import com.rork.varabondhu.ui.theme.PageWhite

/** Bengali-first location list used for both pickup and destination selection. */
@Composable
fun LocationPickerScreen(
    state: LocationUiState,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (Int) -> Unit,
    onPlaceSelected: (LocationPlace) -> Unit,
    onMapPointSelected: (Point) -> Unit,
    onLoadNearbyPlaces: (Context) -> Unit,
    onUseCurrentLocation: (Context) -> Unit,
    onConfirmMapSelection: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMapOpen: Boolean by remember { mutableStateOf(false) }
    val initialRevision: Int = remember { state.selectionRevision }

    LaunchedEffect(state.selectionRevision) {
        if (state.selectionRevision > initialRevision) onNavigateBack()
    }

    AnimatedContent(
        targetState = isMapOpen,
        modifier = modifier.fillMaxSize(),
        transitionSpec = {
            if (targetState) {
                slideInHorizontally { width: Int -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width: Int -> -width / 4 } + fadeOut()
            } else {
                slideInHorizontally { width: Int -> -width / 4 } + fadeIn() togetherWith
                    slideOutHorizontally { width: Int -> width } + fadeOut()
            }
        },
        label = "location-picker-mode"
    ) { showMap: Boolean ->
        if (showMap) {
            FullMapPicker(
                state = state,
                onMapPointSelected = onMapPointSelected,
                onConfirmMapSelection = onConfirmMapSelection,
                onNavigateBack = { isMapOpen = false }
            )
        } else {
            LocationListPicker(
                state = state,
                onQueryChange = onQueryChange,
                onSuggestionSelected = onSuggestionSelected,
                onPlaceSelected = onPlaceSelected,
                onLoadNearbyPlaces = onLoadNearbyPlaces,
                onUseCurrentLocation = onUseCurrentLocation,
                onOpenMap = { isMapOpen = true },
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@Composable
private fun LocationListPicker(
    state: LocationUiState,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (Int) -> Unit,
    onPlaceSelected: (LocationPlace) -> Unit,
    onLoadNearbyPlaces: (Context) -> Unit,
    onUseCurrentLocation: (Context) -> Unit,
    onOpenMap: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context: Context = LocalContext.current
    val selectedPlace: LocationPlace = if (state.activeTarget == LocationTarget.ORIGIN) {
        state.origin
    } else {
        state.destination
    }
    val currentLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        if (permissions.values.any { granted: Boolean -> granted }) onUseCurrentLocation(context)
    }
    val nearbyPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        if (permissions.values.any { granted: Boolean -> granted }) onLoadNearbyPlaces(context)
    }
    val hasLocationPermission: Boolean = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val requestCurrentLocation: () -> Unit = {
        if (hasLocationPermission) {
            onUseCurrentLocation(context)
        } else {
            currentLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            onLoadNearbyPlaces(context)
        } else {
            nearbyPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CardWhite)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PickerHeader(onNavigateBack = onNavigateBack)

        SearchField(
            query = state.query,
            isSearching = state.isSearching,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 142.dp
                )
            ) {
                if (state.query.isNotBlank()) {
                    item(key = "search-heading") {
                        SectionLabel("সার্চের ফলাফল")
                    }
                    if (state.suggestions.isNotEmpty()) {
                        items(state.suggestions, key = LocationSuggestion::id) { suggestion: LocationSuggestion ->
                            LocationRow(
                                title = suggestion.name,
                                subtitle = suggestion.address.takeIf(String::isNotBlank),
                                onClick = { onSuggestionSelected(suggestion.id) }
                            )
                        }
                    } else if (!state.isSearching) {
                        item(key = "search-empty") {
                            EmptySearchMessage(state.errorMessage ?: "কমপক্ষে দুইটি অক্ষর লিখুন")
                        }
                    }
                } else {
                    item(key = "current-heading") {
                        SectionLabel("বর্তমান লোকেশন")
                    }
                    item(key = "current-location") {
                        CurrentLocationRow(
                            currentPlace = state.currentDevicePlace,
                            isLoading = state.isLoadingNearby || state.isResolvingPoint,
                            onClick = requestCurrentLocation
                        )
                    }
                    item(key = "nearby-heading") {
                        SectionLabel(
                            text = "আশেপাশের লোকেশন",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    if (state.isLoadingNearby) {
                        item(key = "nearby-loading") {
                            NearbyLoadingRow()
                        }
                    } else {
                        items(
                            items = state.nearbyPlaces,
                            key = { place: LocationPlace -> "${place.name}-${place.latitude}-${place.longitude}" }
                        ) { place: LocationPlace ->
                            LocationRow(
                                title = place.name,
                                subtitle = place.address.takeIf { address: String -> address != place.name },
                                onClick = { onPlaceSelected(place) }
                            )
                        }
                    }
                    state.errorMessage?.let { message: String ->
                        item(key = "location-error") {
                            Text(
                                text = message,
                                color = DangerRed,
                                fontFamily = BanglaFamily,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
            }

            MiniMapCard(
                center = (state.currentDevicePlace ?: selectedPlace).point,
                onOpenMap = onOpenMap,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun PickerHeader(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "ফিরে যান",
                tint = Ink
            )
        }
        Surface(
            color = BrandGreen.copy(alpha = 0.09f),
            shape = CircleShape,
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = BrandGreen,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(9.dp))
        Text(
            text = "লোকেশন নির্বাচন করুন",
            fontFamily = BanglaFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = Ink
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp),
        singleLine = true,
        placeholder = {
            Text(
                text = "এলাকা, রাস্তা বা ল্যান্ডমার্ক লিখুন",
                fontFamily = BanglaFamily,
                fontSize = 13.sp,
                color = FieldPlaceholder,
                maxLines = 1
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                tint = InkMuted,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            AnimatedVisibility(visible = isSearching) {
                CircularProgressIndicator(
                    color = BrandGreen,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(fontFamily = BanglaFamily, fontSize = 14.sp, color = Ink),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandGreen,
            unfocusedBorderColor = FieldBorder,
            focusedContainerColor = CardWhite,
            unfocusedContainerColor = CardWhite,
            cursorColor = BrandGreen
        )
    )
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontFamily = BanglaFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = InkMuted,
        modifier = modifier.padding(vertical = 10.dp)
    )
}

@Composable
private fun CurrentLocationRow(
    currentPlace: LocationPlace?,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = !isLoading, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = BrandGreen,
            modifier = Modifier.size(25.dp)
        )
        Spacer(modifier = Modifier.width(11.dp))
        Text(
            text = currentPlace?.address ?: "বর্তমান অবস্থান শনাক্ত করা হচ্ছে…",
            fontFamily = BanglaFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = Ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (isLoading) {
            CircularProgressIndicator(
                color = BrandGreen,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.GpsFixed,
                contentDescription = "বর্তমান অবস্থান নিন",
                tint = Ink,
                modifier = Modifier.size(22.dp)
            )
        }
    }
    HorizontalDivider(color = FieldBorder)
}

@Composable
private fun NearbyLoadingRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 22.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            color = BrandGreen,
            strokeWidth = 2.dp,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "আশেপাশের স্থান খোঁজা হচ্ছে…",
            fontFamily = BanglaFamily,
            fontSize = 13.sp,
            color = InkMuted
        )
    }
}

@Composable
private fun LocationRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = BrandGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Ink,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            subtitle?.let { secondaryText: String ->
                Text(
                    text = secondaryText,
                    fontFamily = BanglaFamily,
                    fontSize = 11.sp,
                    color = InkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = Ink,
            modifier = Modifier.size(22.dp)
        )
    }
    HorizontalDivider(color = FieldBorder)
}

@Composable
private fun EmptySearchMessage(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 44.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            fontFamily = BanglaFamily,
            fontSize = 13.sp,
            color = InkMuted
        )
    }
}

@Composable
private fun MiniMapCard(
    center: Point,
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(center)
            zoom(12.4)
        }
    }

    LaunchedEffect(center.latitude(), center.longitude()) {
        viewportState.setCameraOptions {
            center(center)
            zoom(12.4)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(PageWhite)
    ) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = viewportState,
            scaleBar = {},
            compass = {}
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.20f))
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "ম্যাপ থেকে নির্বাচন করুন",
                fontFamily = BanglaFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = Ink
            )
            Button(
                onClick = onOpenMap,
                modifier = Modifier
                    .align(Alignment.End)
                    .height(43.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "ম্যাপ খুলুন",
                    fontFamily = BanglaFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun FullMapPicker(
    state: LocationUiState,
    onMapPointSelected: (Point) -> Unit,
    onConfirmMapSelection: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedPlace: LocationPlace = state.pendingMapPlace ?: if (state.activeTarget == LocationTarget.ORIGIN) {
        state.origin
    } else {
        state.destination
    }
    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(selectedPlace.point)
            zoom(15.0)
        }
    }

    LaunchedEffect(selectedPlace.latitude, selectedPlace.longitude) {
        viewportState.setCameraOptions {
            center(selectedPlace.point)
            zoom(15.0)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PageWhite)
    ) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = viewportState,
            onMapClickListener = { point: Point ->
                onMapPointSelected(point)
                true
            },
            scaleBar = {},
            compass = {}
        ) {
            CircleAnnotation(point = selectedPlace.point) {
                circleColor = BrandGreen
                circleRadius = 9.0
                circleStrokeColor = Color.White
                circleStrokeWidth = 3.0
            }
        }

        Surface(
            onClick = onNavigateBack,
            color = CardWhite.copy(alpha = 0.94f),
            shape = CircleShape,
            shadowElevation = 5.dp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 14.dp, top = 12.dp)
                .size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "লোকেশন তালিকায় ফিরুন",
                    tint = Ink
                )
            }
        }

        Surface(
            color = CardWhite,
            shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
            shadowElevation = 12.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                if (state.isResolvingPoint) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            color = BrandGreen,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(9.dp))
                        Text(
                            text = "ঠিকানা খোঁজা হচ্ছে…",
                            fontFamily = BanglaFamily,
                            fontSize = 13.sp,
                            color = InkMuted
                        )
                    }
                } else {
                    Text(
                        text = selectedPlace.displayName,
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Ink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = selectedPlace.address,
                        fontFamily = BanglaFamily,
                        fontSize = 12.sp,
                        color = InkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(11.dp))
                Button(
                    onClick = onConfirmMapSelection,
                    enabled = !state.isResolvingPoint,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) {
                    Text(
                        text = "এই স্থানটি নিশ্চিত করুন",
                        fontFamily = BanglaFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
