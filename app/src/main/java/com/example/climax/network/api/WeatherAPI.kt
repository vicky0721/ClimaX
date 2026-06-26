package com.example.climax.network.api

import com.example.climax.data.RemoteLocation
import com.example.climax.data.RemoteWeatherData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherAPI {

    companion object{
        const val BASE_URL = "https://api.weatherapi.com/v1/"
        const val API_KEY = "b95f3a0a4b0c4021b3e111947262406"
    }

    @GET("search.json")
    suspend fun searchLocation(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String
    ): Response<List<RemoteLocation>>

    @GET("forecast.json")
    suspend fun getWeatherData(
        @Query("key") key: String = API_KEY,
        @Query("q") query: String
    ): Response<RemoteWeatherData>

}