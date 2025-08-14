package com.example.fishingforecastappstav.mainScreen

import com.example.fishingforecastappstav.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    
    val api: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}