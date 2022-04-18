package com.example.checkpoint

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.checkpoint.dto.WeatherAPI
import com.example.checkpoint.services.IWeatherService
import com.example.checkpoint.services.WeatherService
import kotlinx.coroutines.launch

class MainViewModel(var weatherService: IWeatherService = WeatherService()) : ViewModel(){

    val weather: MutableLiveData<WeatherAPI> = MutableLiveData<WeatherAPI>()
    fun fetchWeather(){
        viewModelScope.launch {
            var feedbackAPI = weatherService.fetchWeather()
            weather.postValue(feedbackAPI)
        }
    }
}