package com.example.climax.dependency_injection

import com.example.climax.network.repository.WeatherDataRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val repositoryModule = module{
    single { WeatherDataRepository(weatherAPI = get()) }
}