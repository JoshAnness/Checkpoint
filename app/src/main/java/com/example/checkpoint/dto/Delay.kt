package com.example.checkpoint.dto

data class Delay(var delay: String = "", var reportID: Int = 0, var delayID : String = "", var location: String = ""){
    override fun toString(): String {
        return "$delay"
    }
}
