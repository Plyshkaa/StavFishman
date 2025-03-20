package com.example.fishingforecastappstav.mainScreen

data class WeatherResponse(
    val weather: List<Weather>,
    val main: Main,
    val name: String
)

data class Weather(
    val description: String,
    val icon: String
)

data class Main(
    val temp: Float,
    val pressure: Int
)
