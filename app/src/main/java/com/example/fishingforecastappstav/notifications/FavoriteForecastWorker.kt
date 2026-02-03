package com.example.fishingforecastappstav.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fishingforecastappstav.R
import com.example.fishingforecastappstav.data.local.FavoritesStore
import com.example.fishingforecastappstav.data.remote.RetrofitInstance
import com.example.fishingforecastappstav.domain.forecast.ForecastUtils
import com.example.fishingforecastappstav.domain.model.WeatherData
import com.example.fishingforecastappstav.utils.Constants
import com.example.fishingforecastappstav.utils.DateUtils
import kotlin.math.roundToInt

class FavoriteForecastWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val favorites = FavoritesStore(applicationContext).getFavorites().toList()
        if (favorites.isEmpty()) return Result.success()

        val response = RetrofitInstance.api.getCurrentWeather(
            Constants.DEFAULT_LATITUDE,
            Constants.DEFAULT_LONGITUDE
        ).execute()

        if (!response.isSuccessful) return Result.retry()

        val current = response.body()?.current ?: return Result.retry()
        val temperature = current.temperature2m ?: return Result.retry()
        val wind = current.windSpeed10m ?: return Result.retry()
        val pressureHpa = current.pressureMsl ?: return Result.retry()

        val weather = WeatherData(
            temperature = temperature,
            pressureMmHg = hPaToMmHg(pressureHpa),
            windSpeed = wind,
            timeOfDay = DateUtils.getCurrentTimeOfDay(),
            season = DateUtils.getSeason(),
            moonPhase = Constants.MoonPhase.NEW_MOON,
            isSpawning = false
        )

        val best = favorites
            .map { fishName ->
                val detail = ForecastUtils.getDetailedForecast(
                    fishName = fishName,
                    temperature = weather.temperature,
                    pressure = weather.pressureMmHg,
                    windSpeed = weather.windSpeed,
                    timeOfDay = weather.timeOfDay,
                    season = weather.season,
                    moonPhase = weather.moonPhase,
                    isSpawning = weather.isSpawning
                )
                fishName to detail
            }
            .maxByOrNull { it.second.finalScore }
            ?: return Result.success()

        ensureChannel()

        val title = "Лучший прогноз сегодня"
        val text = "${best.first}: ${best.second.finalScore}/100 (${best.second.forecastText})"

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_logo1)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(NOTIFICATION_ID, notification)

        return Result.success()
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Прогноз избранных",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
    }

    private fun hPaToMmHg(hPa: Float): Int {
        return (hPa * 0.75006f).roundToInt()
    }

    companion object {
        private const val CHANNEL_ID = "favorite_forecast"
        private const val NOTIFICATION_ID = 1001
    }
}
