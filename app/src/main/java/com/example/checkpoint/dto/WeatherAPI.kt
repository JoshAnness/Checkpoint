package com.example.checkpoint.dto
// To parse the JSON, install Klaxon and do:
//
//   val welcome = Welcome.fromJson(jsonString)

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












