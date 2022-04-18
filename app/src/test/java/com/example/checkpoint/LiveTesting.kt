package com.example.checkpoint

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.checkpoint.dto.WeatherAPI
import com.example.checkpoint.services.WeatherService


import kotlinx.coroutines.test.runTest
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule
class LiveTesting {
    lateinit var weatherService: WeatherService
    lateinit var weatherApi: WeatherAPI
    @Test
    fun getForecast() = runTest{
        givenWeatherIsAvailable()
        whenLocationIsGiven()
        thenObtainWeatherForecast()
    }

    private fun givenWeatherIsAvailable() {
        //Goes to WeatherService Class
        weatherService = WeatherService()
    }
    private suspend fun whenLocationIsGiven() {
        //Goes to WeatherService then inside WeatherService to IWeather
        weatherApi = weatherService.fetchWeather()!!

    }
    private fun thenObtainWeatherForecast() {
        //Then test to find out if it can extract the weather
        assertNotNull(weatherApi)
        assertNotNull(weatherApi.main.temp)

    }
}