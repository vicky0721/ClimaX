package com.example.climax.dependency_injection

import android.location.Location
import com.example.climax.fragments.home.HomeViewModel
import com.example.climax.fragments.location.LocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{
    viewModel{ HomeViewModel(weatherDataRepository = get()) }
    viewModel{ LocationViewModel(weatherDataRepository = get()) }
}