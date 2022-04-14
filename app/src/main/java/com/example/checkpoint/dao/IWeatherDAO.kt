package com.example.checkpoint.dao

import com.example.checkpoint.dto.Weather
import retrofit2.Call
import retrofit2.http.GET

interface IWeatherDAO {

    @GET("/weather?q=cincinnati&units=imperial&appid=5faf2a035a52f392a0394d9a48bc16be")
    fun getWeather() : Call<ArrayList<Weather>>

}