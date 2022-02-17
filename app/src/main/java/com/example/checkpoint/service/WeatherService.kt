package com.example.checkpoint.service
//classes import


//Service import
import com.example.checkpoint.RetrofitClientWeatherAPI
import com.example.checkpoint.dao.IWeather
import com.example.checkpoint.dto.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import retrofit2.http.GET

class WeatherService {
    @GET("weather")
   suspend fun fetchWeather(): List<Weather>?{
       return withContext(Dispatchers.IO){
            val service = RetrofitClientWeatherAPI.retrofitInstance?.create(IWeather :: class.java)
            val weather = async{ service?.getAllWeather()}
            val result = weather.await()?.awaitResponse()?.body()
            return@withContext result
       }
   }

}