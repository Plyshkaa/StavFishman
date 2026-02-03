package com.example.fishingforecastappstav.data.repository

import com.example.fishingforecastappstav.domain.model.WeatherData

interface WeatherRepository {
    fun fetchWeather(latitude: Double, longitude: Double, callback: (Result<WeatherData>) -> Unit)
}
