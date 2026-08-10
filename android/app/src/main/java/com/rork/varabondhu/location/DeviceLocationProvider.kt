package com.rork.varabondhu.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull

/** Reads one fresh device location after the caller has obtained runtime permission. */
object DeviceLocationProvider {
    @SuppressLint("MissingPermission")
    suspend fun currentLocation(context: Context): Location? {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasFineLocation && !hasCoarseLocation) return null

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val cachedLocation = newestLastKnownLocation(locationManager)
        if (cachedLocation?.isFastCacheUsable() == true) return cachedLocation

        val enabledProviders = buildList {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                add(LocationManager.NETWORK_PROVIDER)
            }
            if (hasFineLocation && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                add(LocationManager.GPS_PROVIDER)
            }
        }
        for (provider in enabledProviders) {
            val timeoutMillis = if (provider == LocationManager.NETWORK_PROVIDER) {
                NETWORK_TIMEOUT_MILLIS
            } else {
                GPS_TIMEOUT_MILLIS
            }
            val currentLocation = withTimeoutOrNull(timeoutMillis) {
                locationFromProvider(
                    context = context,
                    locationManager = locationManager,
                    provider = provider
                )
            }
            if (currentLocation?.hasValidCoordinates() == true) return currentLocation
        }
        return cachedLocation?.takeIf { location: Location -> location.hasValidCoordinates() }
    }

    private suspend fun locationFromProvider(
        context: Context,
        locationManager: LocationManager,
        provider: String
    ): Location? = suspendCancellableCoroutine { continuation ->
        val cancellationSignal = CancellationSignal()
        continuation.invokeOnCancellation { cancellationSignal.cancel() }
        LocationManagerCompat.getCurrentLocation(
            locationManager,
            provider,
            cancellationSignal,
            ContextCompat.getMainExecutor(context)
        ) { location: Location? ->
            if (continuation.isActive) continuation.resume(location)
        }
    }

    @SuppressLint("MissingPermission")
    private fun newestLastKnownLocation(locationManager: LocationManager): Location? =
        locationManager.getProviders(true)
            .mapNotNull { provider: String ->
                runCatching { locationManager.getLastKnownLocation(provider) }.getOrNull()
            }
            .filter { location: Location -> location.hasValidCoordinates() }
            .maxByOrNull(Location::getTime)

    private fun Location.isFastCacheUsable(): Boolean {
        val ageMillis = (System.currentTimeMillis() - time).coerceAtLeast(0L)
        val hasUsefulAccuracy = !hasAccuracy() || accuracy <= MAX_FAST_CACHE_ACCURACY_METERS
        return hasValidCoordinates() && ageMillis <= FAST_CACHE_MAX_AGE_MILLIS && hasUsefulAccuracy
    }

    private fun Location.hasValidCoordinates(): Boolean =
        latitude in -90.0..90.0 &&
            longitude in -180.0..180.0 &&
            !(latitude == 0.0 && longitude == 0.0)

    private const val FAST_CACHE_MAX_AGE_MILLIS = 5 * 60 * 1_000L
    private const val MAX_FAST_CACHE_ACCURACY_METERS = 3_000f
    private const val NETWORK_TIMEOUT_MILLIS = 2_000L
    private const val GPS_TIMEOUT_MILLIS = 3_500L
}
