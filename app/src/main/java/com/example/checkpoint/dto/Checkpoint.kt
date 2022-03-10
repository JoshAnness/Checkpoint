package com.example.checkpoint.dto

import android.text.method.DateTimeKeyListener

open class Checkpoint(name: String) {
    constructor(userId: Int,name: String,longitude: String,latitude: Int):this(name){
        fun toString(): String {
            return name
        }
    }
}

open class Map(userId: Int, name: String, longitude: String, latitude: Int, userLocation: String) :
    Checkpoint(userId, name, longitude, latitude) {
    init {
        fun toString(): String {
            return userLocation
        }
    }
}

open class WeatherCondition(
    userId: Int,
    name: String,
    longitude: String,
    latitude: Int,
    userLocation: String,
    weatherId: Int,
    Location: String,
    Radius: String
) : Map(userId, name, longitude, latitude, userLocation) {
    init {
        fun toString(): String {
            return userLocation
        }
    }
}

class WeatherStatus(
    userId: Int,
    name: String,
    longitude: String,
    latitude: Int,
    userLocation: String,
    weatherId: Int,
    Location: String,
    Radius: String,
    statusId: Int,
    temperatureId: Int,
    alert: String,
    time: DateTimeKeyListener
) : WeatherCondition(userId, name, longitude, latitude, userLocation, weatherId, Location, Radius)
