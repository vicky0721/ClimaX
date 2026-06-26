package com.example.climax.fragments.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.climax.data.CurrentLocation
import com.example.climax.data.CurrentWeather
import com.example.climax.data.Forecast
import com.example.climax.data.WeatherData
import com.example.climax.databinding.ItemContainerCurrentLocationBinding
import com.example.climax.databinding.ItemContainerCurrentWeatherBinding
import com.example.climax.databinding.ItemContainerForecastBinding

class WeatherDataAdapter(
    private val onLocationClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val INDEX_CURRENT_LOCATION = 0
        const val INDEX_CURRENT_WEATHER = 1
        const val INDEX_FORECAST = 2
    }

    private val weatherData = mutableListOf<WeatherData>()

    fun setCurrentLocation(currentLocation: CurrentLocation) {
        if (weatherData.isEmpty()) {
            weatherData.add(INDEX_CURRENT_LOCATION, currentLocation)
            notifyItemInserted(INDEX_CURRENT_LOCATION)
        } else {
            weatherData[INDEX_CURRENT_LOCATION] = currentLocation
            notifyItemChanged(INDEX_CURRENT_LOCATION)
        }
    }

    fun setCurrentWeather(currentWeather: CurrentWeather) {
        if (weatherData.size > INDEX_CURRENT_WEATHER) {
            weatherData[INDEX_CURRENT_WEATHER] = currentWeather
            notifyItemChanged(INDEX_CURRENT_WEATHER)
        } else {
            weatherData.add(INDEX_CURRENT_WEATHER, currentWeather)
            notifyItemInserted(INDEX_CURRENT_WEATHER)
        }
    }

    fun setForecastData(forecastList: List<Forecast>) {
        val startIndex = INDEX_FORECAST
        if (weatherData.size > startIndex) {
            weatherData.subList(startIndex, weatherData.size).clear()
        }
        weatherData.addAll(forecastList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            INDEX_CURRENT_LOCATION -> CurrentLocationViewHolder(
                ItemContainerCurrentLocationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            INDEX_FORECAST -> ForecastViewHolder(
                ItemContainerForecastBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> CurrentWeatherViewHolder(
                ItemContainerCurrentWeatherBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CurrentLocationViewHolder -> holder.bind(weatherData[position] as CurrentLocation)
            is CurrentWeatherViewHolder -> holder.bind(weatherData[position] as CurrentWeather)
            is ForecastViewHolder -> holder.bind(weatherData[position] as Forecast)

        }

    }

    override fun getItemCount(): Int {
        return weatherData.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (weatherData[position]) {
            is CurrentLocation -> INDEX_CURRENT_LOCATION
            is CurrentWeather -> INDEX_CURRENT_WEATHER
            is Forecast -> INDEX_FORECAST
        }
    }

    inner class CurrentLocationViewHolder(
        private val binding: ItemContainerCurrentLocationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentLocation: CurrentLocation) {
            with(binding) {
                textCurrentDate.text = currentLocation.date
                textCurrentLocation.text = currentLocation.location
                imageCurrentLocation.setOnClickListener { onLocationClicked() }
                textCurrentLocation.setOnClickListener { onLocationClicked() }
            }
        }
    }

    inner class CurrentWeatherViewHolder(
        private val binding: ItemContainerCurrentWeatherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currentWeather: CurrentWeather) {
            with(binding) {
                imageIcon.load("https:${currentWeather.icon}") {
                    crossfade(true)
                }
                textTemperature.text = String.format("%s\u00B0C", currentWeather.temperature)
                textWind.text = String.format("%s km/h", currentWeather.wind)
                textHumidity.text = String.format("%s%%", currentWeather.humidity)
                textChanceOfRain.text = String.format("%s%%", currentWeather.chanceOfRain)
            }
        }
    }

    inner class ForecastViewHolder(
        private val binding: ItemContainerForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(forecast: Forecast) {
            with(binding) {
                textTime.text = forecast.time
                textTemperature.text = String.format("%s\u00B0C", forecast.temperature)
                textFeelsLikeTemperature.text =
                    String.format("%s\u00B0C", forecast.feelsLikeTemperature)

                imageIcon.load("https:${forecast.icon}") {
                    crossfade(true)
                }
            }
        }
    }

}