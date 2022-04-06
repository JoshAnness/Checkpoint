package com.example.checkpoint.dto

data class Weather (
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
){
    private var CurrentWeather = main + " " + id + " " + description + " " + icon
    override fun toString() : String {
        return CurrentWeather
    }
}