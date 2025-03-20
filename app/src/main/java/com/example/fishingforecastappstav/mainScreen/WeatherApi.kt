package com.example.fishingforecastappstav.mainScreen

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    // Пример запроса: https://api.openweathermap.org/data/2.5/weather?q=Stavropol&appid=YOUR_API_KEY&units=metric&lang=ru
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") city: String = "Stavropol",
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru"
    ): Call<WeatherResponse>
}