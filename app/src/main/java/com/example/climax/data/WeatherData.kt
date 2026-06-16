package com.example.climax.data

sealed class WeatherData

data class CurrentLocation(
    val date:String,
    val location:String = "Chose you location"
): WeatherData()