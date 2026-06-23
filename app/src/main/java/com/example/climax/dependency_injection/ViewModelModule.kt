package com.example.climax.dependency_injection

import com.example.climax.fragments.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module{
    viewModel{ HomeViewModel(weatherDataRepository = get()) }
}