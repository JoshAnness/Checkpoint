package com.example.checkpoint.services

import com.example.checkpoint.RetrofitClientInstance
import com.example.checkpoint.dao.IWeatherDAO
import com.example.checkpoint.dto.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class WeatherService {

    suspend fun fetchWeather(): List<Weather> {
        return withContext(Dispatchers.IO){
            val service = RetrofitClientInstance.retrofitInstance?.create(IWeatherDAO :: class.java)
            val weather = async{ service?.getWeather()}
            val result = weather.await()?.awaitResponse()?.body()!!
            return@withContext result
        }
    }

}