package com.example.checkpoint.dto
import com.google.gson.annotations.SerializedName

data class Weather (@SerializedName("main")var main:String, var description:String){
    override fun toString(): String{
        return description
    }

}
