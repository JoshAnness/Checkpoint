package com.example.checkpoint.dto

import com.google.gson.annotations.SerializedName


data class Weather (
    @SerializedName("coord") var coord: String,
    @SerializedName("lon")var lon : String,
    @SerializedName("lat")var lat : String,
    @SerializedName("id")var id: String,
    @SerializedName("main")var main: String,
    @SerializedName("description")var description: String,
    @SerializedName("icon")var icon : String){
    override fun toString(): String{
        return description
    }

}
