package com.example.checkpoint.dao

import com.example.checkpoint.dto.Weather
import retrofit2.Call
import retrofit2.http.GET

interface IWeather {

    fun getWeather() : Call<ArrayList<Weather>>

}
