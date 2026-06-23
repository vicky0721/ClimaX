package com.example.climax.dependency_injection

import com.example.climax.storage.SharedPreferencesManager
import org.koin.dsl.module

val storageModule = module{
    single { SharedPreferencesManager(get(),get()) }
}