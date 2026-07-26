package com.example.climax.storage

import android.content.Context
import com.example.climax.data.CurrentLocation
import com.example.climax.data.RemoteWeatherData
import com.google.gson.Gson
import androidx.core.content.edit


class SharedPreferencesManager(context: Context, private val gson: Gson) {

    private companion object {
        const val PREF_NAME = "WeatherAppPref"
        const val KEY_CURRENT_LOCATION = "currentLocation"
        const val KEY_WEATHER_DATA = "weatherData"
    }

    private val sharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveCurrentLocation(currentLocation: CurrentLocation) {
        val currentLocationJson = gson.toJson(currentLocation)

        sharedPreferences.edit {
            putString(KEY_CURRENT_LOCATION, currentLocationJson)
        }
    }

    fun getCurrentLocation(): CurrentLocation? {
        return sharedPreferences.getString(
            KEY_CURRENT_LOCATION,
            null
        )?.let { currentLocationJson ->
            gson.fromJson(currentLocationJson, CurrentLocation::class.java)
        }
    }

    fun saveWeatherData(weatherData: RemoteWeatherData) {
        val weatherDataJson = gson.toJson(weatherData)
        sharedPreferences.edit {
            putString(KEY_WEATHER_DATA, weatherDataJson)
        }
    }

    fun getWeatherData(): RemoteWeatherData? {
        return sharedPreferences.getString(KEY_WEATHER_DATA, null)?.let { weatherDataJson ->
            gson.fromJson(weatherDataJson, RemoteWeatherData::class.java)
        }
    }
}