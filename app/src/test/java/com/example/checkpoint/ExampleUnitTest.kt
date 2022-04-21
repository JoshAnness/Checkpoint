package com.example.checkpoint

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.checkpoint.dto.WeatherAPI
import com.example.checkpoint.services.WeatherService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    lateinit var weatherService: WeatherService

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    lateinit var weatherApi: WeatherAPI

    /*
    Requirement 100.0 Weather Forecast
    Scenario:
    As a user wants to know what is going on during the day, I want to be able to see the weather
    projections and updates so I know what is going to happen.
    Dependencies:
    GPS and location will be available and viewable Weather data in the location is also accessible.
    Assumptions:
    Data is stated in english
    Radar is able to be interpreted
    Internet connection at some point of the day
     */
    //Test: Get Weather Forecast
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
        weatherApi = weatherService.fetchWeather("35","139","69702e05c2554c21cf44563eb81ea624", "imperial")!!

    }
    private fun thenObtainWeatherForecast() {
        //Then test to find out if it can extract the weather
        assertNotNull(weatherApi)
    }




}