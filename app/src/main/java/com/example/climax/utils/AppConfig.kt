package com.example.climax.utils

import android.app.Application
import com.example.climax.dependency_injection.repositoryModule
import com.example.climax.dependency_injection.viewModelModule
import org.koin.core.context.startKoin

class AppConfig : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(listOf(repositoryModule, viewModelModule))
        }
    }
}
