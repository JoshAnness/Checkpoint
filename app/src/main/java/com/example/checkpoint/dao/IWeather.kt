package com.example.checkpoint.dao

import com.example.checkpoint.dto.Weather
import retrofit2.Call
import retrofit2.http.GET
interface IWeather {
    //TODO("need to change lat and ln to use the andriod info.")
    //NOTE: The current is a example
    //var lon : String
    //var lat : String
    //fun location(lon : String, lat : String ){
      //  this.lon = lon
       // this.lat = lat
    //}
    //something is not working
    //var websiteLink: String
        //get() = "/data/2.5/weather?lat=$lat&lon=$lon&appid=707f43b75fdd2f4d0ae87ccf8b6764f3"
        //set(value) = TODO("I wish I knew")

    //@GET(websiteLink)
    @GET("/data/2.5/weather?lat=35&lon=139&appid=69702e05c2554c21cf44563eb81ea624")
    fun getAllWeather() : Call<List<Weather>>
}