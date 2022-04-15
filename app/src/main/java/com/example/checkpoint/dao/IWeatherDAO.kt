package com.example.checkpoint.dao

import com.example.checkpoint.dto.Weather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeatherDAO {

    @GET("/weather?")
    fun getWeather(@Query("lon") lon: String, @Query("lat") lat: String) : Call<ArrayList<Weather>>

}