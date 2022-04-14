package com.example.checkpoint

import com.example.checkpoint.services.WeatherService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { MainViewModel(get()) }
    single<WeatherService> { WeatherService() }
}