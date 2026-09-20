package com.example.climax.widget

import android.content.Context
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.climax.R
import com.example.climax.data.ForecastHourRemote
import com.example.climax.data.RemoteWeatherData
import com.example.climax.network.repository.WeatherDataRepository
import com.example.climax.storage.SharedPreferencesManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class ClimaXWidget : GlanceAppWidget(), KoinComponent {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()
    private val weatherDataRepository: WeatherDataRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Read current cache
        val weatherData = sharedPreferencesManager.getWeatherData()

        provideContent {
            WidgetContent(context, weatherData)
        }
    }

    @Composable
    private fun WidgetContent(context: Context, weatherData: RemoteWeatherData?) {
        // Safely extract hours
        val hoursToShow = weatherData?.let { extractNextFiveHours(it) } ?: emptyList()

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color.Transparent)
                .cornerRadius(20.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.Top
        ) {
            // 1. Clock & Date Header (XML)
            Box(
                modifier = GlanceModifier.fillMaxWidth().padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val remoteViews = RemoteViews(context.packageName, R.layout.widget_header_clock).apply {
                    val locationStr = sharedPreferencesManager.getCurrentLocation()?.location ?: "Unknown, Location"
                    val parts = locationStr.split(", ")
                    if (parts.size >= 1) {
                        setTextViewText(R.id.textPlace, parts[0])
                    }
                    if (parts.size >= 2) {
                        setTextViewText(R.id.textStateCountry, parts.drop(1).joinToString(", "))
                    } else {
                        setTextViewText(R.id.textStateCountry, "")
                    }
                }
                AndroidRemoteViews(remoteViews)
            }

            Spacer(modifier = GlanceModifier.size(4.dp))

            // 2. Weather Forecast Section
            if (hoursToShow.isNotEmpty()) {
                Row(
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    hoursToShow.forEach { hour ->
                        Box(
                            modifier = GlanceModifier.defaultWeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            ForecastItem(hour, isNow = hoursToShow.indexOf(hour) == 0)
                        }
                    }
                }
            } else {
                // Informative message if data is missing
                Column(
                    modifier = GlanceModifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Weather Data Found",
                        style = TextStyle(color = ColorProvider(Color.White), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Please open the app and refresh",
                        style = TextStyle(color = ColorProvider(Color.White), fontSize = 10.sp)
                    )
                }
            }
        }
    }

    @Composable
    private fun ForecastItem(hour: ForecastHourRemote, isNow: Boolean) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isNow) "Now" else formatHour(hour.time),
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            )
            Spacer(modifier = GlanceModifier.size(2.dp))
            Image(
                provider = ImageProvider(getWeatherIcon(hour.condition.text, hour.condition.icon)),
                contentDescription = hour.condition.text,
                modifier = GlanceModifier.size(30.dp)
            )
            Spacer(modifier = GlanceModifier.size(2.dp))
            Text(
                text = "${hour.temperature.toInt()}°",
                style = TextStyle(
                    color = ColorProvider(Color.White),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }

    private fun extractNextFiveHours(weatherData: RemoteWeatherData): List<ForecastHourRemote> {
        val allHours = weatherData.forecast.forecastDay.flatMap { it.hour }
        if (allHours.isEmpty()) return emptyList()

        val now = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        // Find current and future hours
        val filtered = allHours.filter {
            try {
                val date = sdf.parse(it.time)
                val itemCal = Calendar.getInstance().apply { time = date!! }
                
                // Compare hour by hour. We want the item if it's today and >= current hour, 
                // or if it's any day after today.
                if (itemCal.get(Calendar.YEAR) > now.get(Calendar.YEAR)) true
                else if (itemCal.get(Calendar.YEAR) < now.get(Calendar.YEAR)) false
                else {
                    if (itemCal.get(Calendar.DAY_OF_YEAR) > now.get(Calendar.DAY_OF_YEAR)) true
                    else if (itemCal.get(Calendar.DAY_OF_YEAR) < now.get(Calendar.DAY_OF_YEAR)) false
                    else itemCal.get(Calendar.HOUR_OF_DAY) >= now.get(Calendar.HOUR_OF_DAY)
                }
            } catch (e: Exception) {
                false
            }
        }

        // Return exactly 5 hours if available
        return if (filtered.size >= 5) {
            filtered.take(5)
        } else {
            // Fallback: if we have less than 5 future hours, just take the first 5 available from the whole list
            allHours.take(5)
        }
    }

    private fun formatHour(time: String): String {
        return try {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(time)
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(date!!).lowercase()
        } catch (e: Exception) {
            "..."
        }
    }

    private fun getWeatherIcon(condition: String, iconUrl: String): Int {
        val lc = condition.lowercase()
        val isNight = iconUrl.contains("night")
        
        return when {
            lc.contains("sunny") || lc.contains("clear") -> if (isNight) R.drawable.ic_moon_yellow else R.drawable.ic_sun_yellow
            lc.contains("thunder") || lc.contains("storm") -> R.drawable.ic_thunder_white
            lc.contains("rain") || lc.contains("drizzle") || lc.contains("shower") -> R.drawable.ic_rain_white
            lc.contains("cloud") || lc.contains("overcast") || lc.contains("mist") || lc.contains("fog") -> R.drawable.ic_cloud_white
            lc.contains("snow") || lc.contains("sleet") || lc.contains("ice") -> R.drawable.ic_snow_white
            else -> if (isNight) R.drawable.ic_moon_yellow else R.drawable.ic_sun_yellow
        }
    }
}
