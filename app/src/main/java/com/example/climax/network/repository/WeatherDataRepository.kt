package com.example.climax.network.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.example.climax.data.CurrentLocation
import com.example.climax.data.RemoteLocation
import com.example.climax.data.RemoteWeatherData
import com.example.climax.network.api.WeatherAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.Priority
import retrofit2.http.Query


class WeatherDataRepository(private val weatherAPI: WeatherAPI) {

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation) -> Unit,
        onFailure: () -> Unit
    ) {
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location == null) {
                onFailure()
                return@addOnSuccessListener
            }

            onSuccess(
                CurrentLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }.addOnFailureListener {
            onFailure()
        }

    }

        @Suppress("DEPRECATION")
        fun updateAddressText(
            currentLocation: CurrentLocation,
            geocoder: Geocoder
        ): CurrentLocation {

            val latitude = currentLocation.latitude ?: return currentLocation
            val longitude = currentLocation.longitude ?: return currentLocation

            return geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
                val address = addresses[0]
                val addressText = StringBuilder()

                addressText.append(address.locality).append(", ")
                addressText.append(address.adminArea).append(", ")
                addressText.append(address.countryName)

                currentLocation.copy(
                    location = addressText.toString()
                )
            } ?: currentLocation
        }

    suspend fun searchLocation(query: String): List<RemoteLocation>? {
        val response = weatherAPI.searchLocation(query = query)
        return if(response.isSuccessful) response.body() else null
    }

    suspend fun getWeatherData(latitude: Double, longitude: Double): RemoteWeatherData? {
        val response = weatherAPI.getWeatherData(query = "$latitude,$longitude")
        return if (response.isSuccessful) response.body() else null
    }

}