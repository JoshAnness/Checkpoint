package com.example.checkpoint.dto

import com.google.gson.annotations.SerializedName

data class Report(@SerializedName("description") var description: String = "", var latitude: String = "", var longitude: String = "", var id: Int = 0) {

    override fun toString(): String {
        return "$description"
    }
}
