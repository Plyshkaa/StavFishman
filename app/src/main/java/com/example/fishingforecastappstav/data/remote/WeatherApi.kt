package com.example.fishingforecastappstav.data.remote

import com.example.fishingforecastappstav.data.remote.dto.OpenMeteoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    // Example: /v1/forecast?latitude=45.04&longitude=41.97&current=temperature_2m,wind_speed_10m,pressure_msl&wind_speed_unit=ms&timezone=auto
    @GET("v1/forecast")
    fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,wind_speed_10m,pressure_msl",
        @Query("wind_speed_unit") windSpeedUnit: String = "ms",
        @Query("timezone") timezone: String = "auto"
    ): Call<OpenMeteoResponse>
}
