package com.example.checkpoint.dto

data class Delay(var delayName: String = "", var reportID: Int = 0, var delayID : String = "", var latitude: String = "", var longitude: String = ""){
    override fun toString(): String {
        return "$delayName"
    }
}
