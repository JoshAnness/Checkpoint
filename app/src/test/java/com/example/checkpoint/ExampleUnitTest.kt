package com.example.checkpoint

import org.junit.Test

import org.junit.Assert.*

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
    fun getForecast(){
        givenWeatherIsAvailable()
        whenLocationIsGiven()
        thenObtainWeatherForecast()
    }
    private fun thenObtainWeatherForecast() {
        TODO("Not yet implemented")
    }

    private fun whenLocationIsGiven() {
        TODO("Not yet implemented")
    }

    private fun givenWeatherIsAvailable() {
        TODO("Not yet implemented")
    }
    //Test: Weather Warning
    @Test
    fun getWeatherWarnings(){
        givenAreaOfWeatherWarning()
        thenObtainWeatherWarning()
    }


    private fun givenAreaOfWeatherWarning() {
        TODO("Not yet implemented")
    }
    private fun thenObtainWeatherWarning() {
        TODO("Not yet implemented")
    }
    //Test: Get weather events ahead of time (24 hours before)
    @Test
    fun getWeatherEventsAtCertainTime(){
        givenWeatherEventWillHappenAtCertainTime()
        thenGivenWarningAheadOfTime()
    }


    private fun givenWeatherEventWillHappenAtCertainTime() {
        TODO("Not yet implemented")
    }
    private fun thenGivenWarningAheadOfTime() {
        TODO("Not yet implemented")
    }
    //Test: Give the last weather report that was made if internet is not available
    fun getLastSavedWeatherReport(){
        givenNoInternetConnection()
        thenShowLastWeatherReport()
    }

    private fun givenNoInternetConnection() {
        TODO("Not yet implemented")
    }

    private fun thenShowLastWeatherReport() {
        TODO("Not yet implemented")
    }

    /*
    Requirement 101.0 Driving Events
    Scenario:
    As a user that is driving, I want to know about events around my route, so that I can plan accordingly.
    Dependencies:
    GPS data is available, and user has granted location access.
    The devices know it is in a car.
     */

}