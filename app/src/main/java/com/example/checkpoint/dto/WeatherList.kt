package com.example.checkpoint.dto

import com.example.checkpoint.dao.IWeather
import com.google.gson.annotations.SerializedName

data class WeatherList(@SerializedName("weather") var weather: List<IWeather>) {


}