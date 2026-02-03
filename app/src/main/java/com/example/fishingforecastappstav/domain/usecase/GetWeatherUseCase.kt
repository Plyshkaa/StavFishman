package com.example.fishingforecastappstav.domain.usecase

import com.example.fishingforecastappstav.data.repository.WeatherRepository
import com.example.fishingforecastappstav.domain.model.WeatherData

class GetWeatherUseCase(private val repository: WeatherRepository) {
    operator fun invoke(latitude: Double, longitude: Double, callback: (Result<WeatherData>) -> Unit) {
        repository.fetchWeather(latitude, longitude, callback)
    }
}
