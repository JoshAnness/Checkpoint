package com.example.checkpoint.dto

import android.text.method.DateTimeKeyListener

open class Route(
    userId: Int,
    name: String,
    longitude: String,
    latitude: Int,
    userLocation: String,
    routeTypeId: Int,
    destination: String
) : Map(userId, name, longitude, latitude, userLocation)

class Event:Route{
    constructor(userId: Int,name: String,longitude: String,latitude: Int,userLocation: String,routeTypeId: Int,destination: String,eventId:Int,Location:String,Time:DateTimeKeyListener):
            super(userId,name,longitude,latitude,userLocation,routeTypeId,destination)
}