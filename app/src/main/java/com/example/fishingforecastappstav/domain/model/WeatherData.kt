package com.example.fishingforecastappstav.domain.model

data class WeatherData(
    val temperature: Float,
    val pressureMmHg: Int,
    val windSpeed: Float,
    val timeOfDay: String,
    val season: String,
    val moonPhase: String,
    val isSpawning: Boolean
)
