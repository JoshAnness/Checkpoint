package com.example.checkpoint

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientWeatherAPI {
    private var retrofit: Retrofit? = null
    private  val BASE_URL = "http://api.openweathermap.org"

    val retrofitInstance : Retrofit?

        get(){
            if(retrofit == null){
                retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()


            }
            return retrofit
        }
    fun getClient(baseUrl:String):Retrofit {
        if (retrofit == null)
        {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

}