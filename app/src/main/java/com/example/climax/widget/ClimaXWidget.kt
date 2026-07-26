package com.example.climax.widget

import android.content.Context
import android.widget.RemoteViews
import androidx.glance.GlanceId
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import com.example.climax.R
import com.example.climax.storage.SharedPreferencesManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ClimaXWidget : GlanceAppWidget(), KoinComponent {

    private val sharedPreferencesManager: SharedPreferencesManager by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val weatherData = sharedPreferencesManager.getWeatherData()
        
        provideContent {
            val remoteViews = RemoteViews(context.packageName, R.layout.layout_climax_widget)
            
            weatherData?.let {
                val tempText = "${it.current.temperature.toInt()}°C"
                remoteViews.setTextViewText(R.id.textTemperature, tempText)
                remoteViews.setTextViewText(R.id.textCondition, it.current.condition.text)
            } ?: run {
                remoteViews.setTextViewText(R.id.textTemperature, "--°C")
                remoteViews.setTextViewText(R.id.textCondition, "Loading...")
            }
            
            AndroidRemoteViews(remoteViews)
        }
    }
}
