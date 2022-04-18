package com.example.checkpoint.services

import com.example.checkpoint.RetrofitClientWeatherAPI
import com.example.checkpoint.dao.IWeather
import com.example.checkpoint.dto.WeatherAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
interface IWeatherService {
    suspend fun fetchWeather(): WeatherAPI?
}
class WeatherService : IWeatherService {
    override suspend fun fetchWeather(): WeatherAPI?{
        return withContext(Dispatchers.IO){
            val service = RetrofitClientWeatherAPI.retrofitInstance?.create(IWeather :: class.java)
            val weather = async{ service?.getAllWeather()}
            val result = weather.await()?.awaitResponse()?.body()
            return@withContext result
        }
    }



}