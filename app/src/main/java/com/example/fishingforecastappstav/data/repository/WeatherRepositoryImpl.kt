package com.example.fishingforecastappstav.data.repository

import android.util.Log
import com.example.fishingforecastappstav.data.remote.RetrofitInstance
import com.example.fishingforecastappstav.data.remote.dto.OpenMeteoResponse
import com.example.fishingforecastappstav.domain.model.WeatherData
import com.example.fishingforecastappstav.utils.Constants
import com.example.fishingforecastappstav.utils.DateUtils
import java.io.IOException
import kotlin.math.roundToInt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepositoryImpl : WeatherRepository {

    override fun fetchWeather(latitude: Double, longitude: Double, callback: (Result<WeatherData>) -> Unit) {
        RetrofitInstance.api.getCurrentWeather(latitude, longitude)
            .enqueue(object : Callback<OpenMeteoResponse> {
                override fun onResponse(call: Call<OpenMeteoResponse>, response: Response<OpenMeteoResponse>) {
                    if (!response.isSuccessful) {
                        callback(Result.failure(IllegalStateException("Ошибка загрузки погоды: ${response.code()}")))
                        return
                    }

                    val body = response.body()
                    val current = body?.current
                    if (current == null) {
                        callback(Result.failure(IllegalStateException("Пустой ответ от сервера")))
                        return
                    }

                    val temperature = current.temperature2m
                    val windSpeed = current.windSpeed10m
                    val pressureHpa = current.pressureMsl

                    if (temperature == null || windSpeed == null || pressureHpa == null) {
                        callback(Result.failure(IllegalStateException("Недостаточно данных в ответе сервера")))
                        return
                    }

                    val data = WeatherData(
                        temperature = temperature,
                        pressureMmHg = hPaToMmHg(pressureHpa),
                        windSpeed = windSpeed,
                        timeOfDay = DateUtils.getCurrentTimeOfDay(),
                        season = DateUtils.getSeason(),
                        moonPhase = Constants.MoonPhase.NEW_MOON, // Заглушка
                        isSpawning = false // Заглушка
                    )
                    callback(Result.success(data))
                }

                override fun onFailure(call: Call<OpenMeteoResponse>, t: Throwable) {
                    val message = if (t is IOException) {
                        "${t::class.java.simpleName}: ${t.message}"
                    } else {
                        t.message ?: "Неизвестная ошибка"
                    }
                    Log.e("WeatherRepository", "Ошибка сети", t)
                    callback(Result.failure(IllegalStateException(message, t)))
                }
            })
    }

    private fun hPaToMmHg(hPa: Float): Int {
        return (hPa * 0.75006f).roundToInt()
    }
}
