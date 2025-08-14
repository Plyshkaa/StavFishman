package com.example.fishingforecastappstav.weather

import com.example.fishingforecastappstav.mainScreen.RetrofitInstance
import com.example.fishingforecastappstav.mainScreen.WeatherResponse
import com.example.fishingforecastappstav.utils.Constants
import com.example.fishingforecastappstav.utils.DateUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherManager {
    
    data class WeatherData(
        val temperature: Float,
        val pressure: Int,
        val windSpeed: Float,
        val timeOfDay: String,
        val season: String,
        val moonPhase: String,
        val isSpawning: Boolean
    )
    
    interface WeatherCallback {
        fun onWeatherLoaded(weatherData: WeatherData)
        fun onWeatherError(error: String)
    }
    
    fun fetchWeather(callback: WeatherCallback) {
        RetrofitInstance.api.getCurrentWeather(Constants.DEFAULT_CITY, Constants.WEATHER_API_KEY)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        weatherData?.let {
                            val data = WeatherData(
                                temperature = it.main.temp,
                                pressure = it.main.pressure,
                                windSpeed = 3.5f, // Заглушка, так как API не возвращает скорость ветра
                                timeOfDay = DateUtils.getCurrentTimeOfDay(),
                                season = DateUtils.getSeason(),
                                moonPhase = "New Moon", // Заглушка
                                isSpawning = false // Заглушка
                            )
                            callback.onWeatherLoaded(data)
                        } ?: callback.onWeatherError("Пустой ответ от сервера")
                    } else {
                        callback.onWeatherError("Ошибка загрузки погоды: ${response.code()}")
                    }
                }
                
                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    callback.onWeatherError("Ошибка сети: ${t.message}")
                }
            })
    }
    
    fun formatWeatherInfo(weatherData: WeatherData): String {
        return StringBuilder().apply {
            appendLine("🌤️ Погода и условия для расчёта:")
            appendLine("• Температура: ${weatherData.temperature}°C")
            appendLine("• Давление: ${weatherData.pressure} мм рт. ст.")
            appendLine("• Ветер: ${weatherData.windSpeed} м/с")
            appendLine("• Время суток: ${DateUtils.getTimeOfDayInRussian(weatherData.timeOfDay)}")
            appendLine("• Сезон: ${DateUtils.getSeasonInRussian(weatherData.season)}")
            appendLine("• Фаза луны: ${DateUtils.getMoonPhaseInRussian(weatherData.moonPhase)}")
            appendLine("• Нерест: ${DateUtils.getSpawningInRussian(weatherData.isSpawning)}")
        }.toString()
    }
}
