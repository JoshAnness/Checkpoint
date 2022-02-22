package com.example.checkpoint.dto
// To parse the JSON, install Klaxon and do:
//
//   val welcome = Welcome.fromJson(jsonString)

import com.beust.klaxon.Json


data class WeatherAPI (
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long
)

data class Clouds (
    val all: Long
)

data class Coord (
    val lon: Long,
    val lat: Long
)

data class Main (
    val temp: Double,

    @Json(name = "feels_like")
    val feelsLike: Double,

    @Json(name = "temp_min")
    val tempMin: Double,

    @Json(name = "temp_max")
    val tempMax: Double,

    val pressure: Long,
    val humidity: Long
)

data class Sys (
    val type: Long,
    val id: Long,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Weather (
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind (
    val speed: Double,
    val deg: Long,
    val gust: Double
)
