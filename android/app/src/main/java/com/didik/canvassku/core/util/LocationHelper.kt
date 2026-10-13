package com.didik.canvassku.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import org.koin.core.annotation.Singleton
import kotlin.coroutines.resume

data class GeoPoint(val latitude: Double, val longitude: Double)

@Singleton
class LocationHelper(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context.applicationContext)

    @SuppressLint("MissingPermission")
    suspend fun lockCurrentLocation(): Result<GeoPoint> {
        awaitCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY)?.let { return Result.success(it) }
        awaitCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY)?.let { return Result.success(it) }
        awaitLastLocation()?.let { return Result.success(it.toGeoPoint()) }
        withTimeoutOrNull(SINGLE_UPDATE_TIMEOUT_MS) { awaitSingleUpdate() }
            ?.let { return Result.success(it) }
        return Result.failure(
            IllegalStateException("Lokasi tidak tersedia. Pastikan GPS aktif atau set lokasi di emulator."),
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun awaitLastLocation(): Location? =
        suspendCancellableCoroutine { cont ->
            client.lastLocation
                .addOnSuccessListener { location ->
                    if (cont.isActive) cont.resume(location)
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(null)
                }
        }

    @SuppressLint("MissingPermission")
    private suspend fun awaitCurrentLocation(priority: Int): GeoPoint? =
        suspendCancellableCoroutine { cont ->
            val cancellation = CancellationTokenSource()
            cont.invokeOnCancellation { cancellation.cancel() }
            client.getCurrentLocation(priority, cancellation.token)
                .addOnSuccessListener { location ->
                    if (cont.isActive) cont.resume(location?.toGeoPoint())
                }
                .addOnFailureListener {
                    if (cont.isActive) cont.resume(null)
                }
        }

    @SuppressLint("MissingPermission")
    private suspend fun awaitSingleUpdate(): GeoPoint? =
        suspendCancellableCoroutine { cont ->
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L)
                .setMaxUpdates(1)
                .setDurationMillis(SINGLE_UPDATE_TIMEOUT_MS)
                .build()
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    client.removeLocationUpdates(this)
                    if (cont.isActive) cont.resume(location.toGeoPoint())
                }
            }
            cont.invokeOnCancellation { client.removeLocationUpdates(callback) }
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
                .addOnFailureListener {
                    client.removeLocationUpdates(callback)
                    if (cont.isActive) cont.resume(null)
                }
        }

    private fun Location.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)

    private companion object {
        const val SINGLE_UPDATE_TIMEOUT_MS = 12_000L
    }
}
