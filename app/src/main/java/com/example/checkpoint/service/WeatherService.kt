package com.example.checkpoint.service
//classes import
import com.example.checkpoint.RetrofitClientWeatherAPI
import com.example.checkpoint.dao.IWeather
import com.example.checkpoint.dto.Weather


//Service import
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class WeatherService {
   suspend fun fetchWeather(): List<Weather>?{
       return withContext(Dispatchers.IO){
            val service = RetrofitClientWeatherAPI.retrofitInstance?.create(IWeather :: class.java)
            val weather = async{ service?.getAllWeather()}
            val result = weather.await()?.awaitResponse()?.body()
            return@withContext result
       }
   }

}