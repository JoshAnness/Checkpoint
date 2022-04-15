package com.example.checkpoint.dto

import com.google.gson.annotations.SerializedName

data class Weather (@SerializedName("coord") var coord: Coord, @SerializedName("weather") var weather: WeatherOBJ, @SerializedName("main") var main: Main)

class Coord {
    @SerializedName("lon") var lon: Float = 0F
    @SerializedName("lat") var lat: Float = 0F
}

class WeatherOBJ {
    @SerializedName("id") var id: Int = 0
    @SerializedName("main") var main: String? = null
    @SerializedName("description") var description: String? = null
    @SerializedName("icon") var icon: String? = null
}

class Main {
    @SerializedName("temp") var temp: Float = 0F
    @SerializedName("feels_like") var feelsLike: Float = 0F
    @SerializedName("temp_min") var tempMin: Float = 0F
    @SerializedName("temp_max") var tempMax: Float = 0F
    @SerializedName("pressure") var pressure: Float = 0F
    @SerializedName("humidity") var humidity: Float = 0F
}