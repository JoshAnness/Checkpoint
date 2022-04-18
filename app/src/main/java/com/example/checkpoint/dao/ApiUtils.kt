package com.example.checkpoint.dao

import com.example.checkpoint.RetrofitClientWeatherAPI

object ApiUtils {


    val apiService: IWeather
        get() {
            return RetrofitClientWeatherAPI.getClient("https://api.openweathermap.org/").create(IWeather::class.java)
        }
}