package com.example.fishingforecastappstav.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fishingforecastappstav.domain.model.WeatherData
import com.example.fishingforecastappstav.domain.usecase.GetWeatherUseCase
import com.example.fishingforecastappstav.mainScreen.Fish
import com.example.fishingforecastappstav.domain.forecast.ForecastUtils

sealed class WeatherState {
    object Loading : WeatherState()
    data class Success(val data: WeatherData) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

class MainViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    private var currentWeather: WeatherData? = null

    fun loadWeather(latitude: Double, longitude: Double) {
        _weatherState.value = WeatherState.Loading
        getWeatherUseCase(latitude, longitude) { result ->
            result
                .onSuccess { data ->
                    currentWeather = data
                    Log.d(
                        "WeatherDebug",
                        "temp=${data.temperature}C pressure=${data.pressureMmHg}mmHg wind=${data.windSpeed}m/s timeOfDay=${data.timeOfDay} season=${data.season}"
                    )
                    _weatherState.postValue(WeatherState.Success(data))
                }
                .onFailure { error ->
                    _weatherState.postValue(WeatherState.Error(error.message ?: "Неизвестная ошибка"))
                }
        }
    }

    fun calculateForecast(fish: Fish): ForecastUtils.DetailedForecast? {
        val weather = currentWeather ?: return null
        return ForecastUtils.getDetailedForecast(
            fishName = fish.name,
            temperature = weather.temperature,
            pressure = weather.pressureMmHg,
            windSpeed = weather.windSpeed,
            timeOfDay = weather.timeOfDay,
            season = weather.season,
            moonPhase = weather.moonPhase,
            isSpawning = weather.isSpawning
        )
    }
}
