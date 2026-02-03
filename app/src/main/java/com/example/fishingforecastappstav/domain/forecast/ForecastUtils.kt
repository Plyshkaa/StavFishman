package com.example.fishingforecastappstav.domain.forecast

import android.graphics.Color
import com.example.fishingforecastappstav.utils.Constants

object ForecastUtils {

    private data class FishProfile(
        val minTemp: Float,
        val maxTemp: Float,
        val badSeasons: Set<String>,
        val winterScoreCap: Int,
        val coldScoreCap: Int,
        val isColdActive: Boolean
    )

    private val fishProfiles = mapOf(
        "Окунь" to FishProfile(minTemp = -5f, maxTemp = 24f, badSeasons = emptySet(), winterScoreCap = 75, coldScoreCap = 60, isColdActive = true),
        "Щука" to FishProfile(minTemp = -8f, maxTemp = 22f, badSeasons = emptySet(), winterScoreCap = 80, coldScoreCap = 65, isColdActive = true),
        "Лещ" to FishProfile(minTemp = 8f, maxTemp = 25f, badSeasons = setOf(Constants.Season.WINTER), winterScoreCap = 20, coldScoreCap = 25, isColdActive = false),
        "Карась" to FishProfile(minTemp = 12f, maxTemp = 28f, badSeasons = setOf(Constants.Season.WINTER), winterScoreCap = 10, coldScoreCap = 20, isColdActive = false),
        "Плотва" to FishProfile(minTemp = 4f, maxTemp = 22f, badSeasons = setOf(Constants.Season.WINTER), winterScoreCap = 20, coldScoreCap = 25, isColdActive = false),
        "Судак" to FishProfile(minTemp = -6f, maxTemp = 18f, badSeasons = emptySet(), winterScoreCap = 80, coldScoreCap = 65, isColdActive = true),
        "Сазан" to FishProfile(minTemp = 14f, maxTemp = 30f, badSeasons = setOf(Constants.Season.WINTER), winterScoreCap = 10, coldScoreCap = 15, isColdActive = false)
    )
    
    // Базовый расчёт баллов клева (максимум 100 баллов)
    fun calculateCatchScore(
        temp: Float,
        pressure: Int,
        windSpeed: Float,
        timeOfDay: String,
        season: String,
        moonPhase: String,
        isSpawning: Boolean
    ): Int {
        // Нормированные оценки факторов (0..100)
        val tempScore = when {
            temp < 0f -> 0
            temp in 0f..4f -> 15
            temp in 5f..9f -> 35
            temp in 10f..14f -> 60
            temp in 15f..22f -> 100
            temp in 23f..25f -> 80
            temp in 26f..30f -> 50
            temp in 31f..35f -> 25
            else -> 0
        }

        val windScore = when {
            windSpeed in 2f..5f -> 100
            windSpeed in 1f..7f -> 70
            windSpeed in 0f..10f -> 40
            else -> 20
        }

        val pressureScore = when {
            pressure in 750..770 -> 100
            pressure in 740..749 || pressure in 771..780 -> 70
            pressure in 730..739 || pressure in 781..790 -> 40
            else -> 20
        }

        val timeScore = when (timeOfDay) {
            Constants.TimeOfDay.MORNING -> 100
            Constants.TimeOfDay.EVENING -> 80
            Constants.TimeOfDay.DAY -> 60
            Constants.TimeOfDay.NIGHT -> 40
            else -> 50
        }

        val seasonScore = when (season) {
            Constants.Season.AUTUMN -> 100
            Constants.Season.SPRING -> 80
            Constants.Season.SUMMER -> 60
            Constants.Season.WINTER -> 40
            else -> 50
        }

        val moonScore = when (moonPhase) {
            Constants.MoonPhase.NEW_MOON -> 100
            Constants.MoonPhase.FULL_MOON -> 80
            Constants.MoonPhase.FIRST_QUARTER -> 60
            Constants.MoonPhase.LAST_QUARTER -> 40
            else -> 50
        }

        // Веса факторов (сумма = 1.0)
        val score = (tempScore * 0.35f +
            pressureScore * 0.20f +
            windScore * 0.15f +
            timeScore * 0.15f +
            seasonScore * 0.10f +
            moonScore * 0.05f).toInt()

        // Нерест (штраф)
        val adjustedScore = if (isSpawning) score - 20 else score

        return adjustedScore.coerceIn(0, 100)
    }
    
    // Улучшенный динамический расчёт коэффициента для конкретной рыбы
    fun getFishFactorDynamic(
        fishName: String,
        temperature: Float,
        pressure: Int,
        windSpeed: Float,
        timeOfDay: String,
        season: String,
        moonPhase: String,
        isSpawning: Boolean
    ): Float {
        val baseFactor = when (fishName) {
            "Окунь" -> 1.0f
            "Щука" -> 1.1f
            "Лещ" -> 0.9f
            "Карась" -> 1.0f
            "Плотва" -> 1.05f
            "Судак" -> 1.15f
            "Сазан" -> 1.0f
            else -> 1.0f
        }
        
        var dynamicFactor = baseFactor
        
        // Улучшенные специфичные корректировки для каждой рыбы
        when (fishName) {
            "Окунь" -> {
                // Окунь любит прохладную воду и утренние часы
                when {
                    temperature in 12f..18f -> dynamicFactor += 0.15f
                    temperature > 25f -> dynamicFactor -= 0.2f
                    temperature < 5f -> dynamicFactor -= 0.15f
                }
                when (timeOfDay) {
                    Constants.TimeOfDay.MORNING -> dynamicFactor += 0.1f
                    Constants.TimeOfDay.EVENING -> dynamicFactor += 0.05f
                    Constants.TimeOfDay.NIGHT -> dynamicFactor -= 0.1f
                }
                when (season) {
                    Constants.Season.SPRING -> dynamicFactor += 0.1f
                    Constants.Season.AUTUMN -> dynamicFactor += 0.05f
                    Constants.Season.WINTER -> dynamicFactor += 0.1f
                }
            }
            
            "Щука" -> {
                // Щука - хищник, активна в сумерки и при умеренной температуре
                when {
                    temperature in 8f..15f -> dynamicFactor += 0.2f
                    temperature > 25f -> dynamicFactor -= 0.25f
                    temperature < 2f -> dynamicFactor -= 0.2f
                }
                when (timeOfDay) {
                    Constants.TimeOfDay.EVENING -> dynamicFactor += 0.15f
                    Constants.TimeOfDay.NIGHT -> dynamicFactor += 0.1f
                    Constants.TimeOfDay.DAY -> dynamicFactor -= 0.1f
                }
                when (season) {
                    Constants.Season.AUTUMN -> dynamicFactor += 0.15f
                    Constants.Season.SPRING -> dynamicFactor += 0.1f
                    Constants.Season.WINTER -> dynamicFactor += 0.2f
                }
            }
            
            "Лещ" -> {
                // Лещ любит стабильное давление и умеренную температуру
                when {
                    temperature in 15f..22f -> dynamicFactor += 0.15f
                    temperature > 28f -> dynamicFactor -= 0.2f
                    temperature < 8f -> dynamicFactor -= 0.15f
                }
                when {
                    pressure in 755..765 -> dynamicFactor += 0.15f
                    pressure > 775 -> dynamicFactor -= 0.1f
                    pressure < 745 -> dynamicFactor -= 0.1f
                }
                when (season) {
                    Constants.Season.SPRING -> dynamicFactor += 0.1f
                    Constants.Season.AUTUMN -> dynamicFactor += 0.05f
                    Constants.Season.WINTER -> dynamicFactor -= 0.2f
                }
            }
            
            "Карась" -> {
                // Карась устойчив к неблагоприятным условиям, любит тепло
                when {
                    temperature in 18f..26f -> dynamicFactor += 0.2f
                    temperature in 12f..30f -> dynamicFactor += 0.1f
                    temperature < 8f -> dynamicFactor -= 0.15f
                }
                when {
                    windSpeed < 5f -> dynamicFactor += 0.1f
                    windSpeed > 10f -> dynamicFactor -= 0.15f
                }
                when (season) {
                    Constants.Season.SUMMER -> dynamicFactor += 0.15f
                    Constants.Season.SPRING -> dynamicFactor += 0.1f
                    Constants.Season.WINTER -> dynamicFactor -= 0.2f
                }
            }
            
            "Плотва" -> {
                // Плотва активна осенью и днем
                when (season) {
                    Constants.Season.AUTUMN -> dynamicFactor += 0.2f
                    Constants.Season.SPRING -> dynamicFactor += 0.1f
                    Constants.Season.WINTER -> dynamicFactor -= 0.2f
                }
                when (timeOfDay) {
                    Constants.TimeOfDay.DAY -> dynamicFactor += 0.15f
                    Constants.TimeOfDay.MORNING -> dynamicFactor += 0.1f
                    Constants.TimeOfDay.NIGHT -> dynamicFactor -= 0.1f
                }
                when {
                    temperature in 12f..20f -> dynamicFactor += 0.1f
                    temperature > 25f -> dynamicFactor -= 0.1f
                }
            }
            
            "Судак" -> {
                // Судак - ночной хищник, любит прохладную воду
                when (timeOfDay) {
                    Constants.TimeOfDay.NIGHT -> dynamicFactor += 0.25f
                    Constants.TimeOfDay.EVENING -> dynamicFactor += 0.15f
                    Constants.TimeOfDay.DAY -> dynamicFactor -= 0.15f
                }
                when {
                    temperature in 10f..18f -> dynamicFactor += 0.2f
                    temperature > 22f -> dynamicFactor -= 0.2f
                    temperature < 5f -> dynamicFactor -= 0.15f
                }
                when (season) {
                    Constants.Season.AUTUMN -> dynamicFactor += 0.15f
                    Constants.Season.SPRING -> dynamicFactor += 0.1f
                    Constants.Season.SUMMER -> dynamicFactor -= 0.2f
                    Constants.Season.WINTER -> dynamicFactor += 0.25f
                }
            }
            
            "Сазан" -> {
                // Сазан любит тепло и спокойную воду
                when {
                    temperature in 20f..28f -> dynamicFactor += 0.2f
                    temperature in 15f..30f -> dynamicFactor += 0.1f
                    temperature < 12f -> dynamicFactor -= 0.2f
                }
                when {
                    windSpeed < 3f -> dynamicFactor += 0.15f
                    windSpeed > 8f -> dynamicFactor -= 0.15f
                }
                when (season) {
                    Constants.Season.SUMMER -> dynamicFactor += 0.2f
                    Constants.Season.SPRING -> dynamicFactor += 0.1f
                    Constants.Season.WINTER -> dynamicFactor -= 0.25f
                }
            }
        }
        
        // Общие корректировки
        if (isSpawning) dynamicFactor -= 0.3f  // Увеличенный штраф за нерест
        if (moonPhase == Constants.MoonPhase.NEW_MOON) dynamicFactor += 0.1f
        if (moonPhase == Constants.MoonPhase.FULL_MOON) dynamicFactor += 0.05f
        
        // Ограничиваем диапазон [0.3, 1.8] - расширенный диапазон
        return dynamicFactor.coerceIn(0.3f, 1.8f)
    }
    
    // Улучшенное преобразование баллов в текст прогноза
    fun getForecastText(score: Int): String {
        return when {
            score >= 85 -> Constants.Forecast.EXCELLENT
            score >= 65 -> Constants.Forecast.GOOD
            score >= 40 -> "Средний"
            else -> Constants.Forecast.POOR
        }
    }
    
    // Получение цвета для прогноза
    fun getForecastColor(forecastText: String): Int {
        return when (forecastText) {
            Constants.Forecast.POOR -> Color.parseColor("#F49292")   // нежно-красный
            "Средний" -> Color.parseColor("#FFD700")                 // золотой
            Constants.Forecast.GOOD -> Color.parseColor("#FFE599")   // нежно-жёлтый
            Constants.Forecast.EXCELLENT -> Color.parseColor("#B6D7A8")// нежно-зелёный
            else -> Color.WHITE
        }
    }
    
    // Получение цвета для активности в календаре
    fun getColorForActivity(activity: Int): Int {
        return when (activity) {
            0 -> Color.parseColor("#FFFFFF")
            1 -> Color.parseColor("#D9EAD3")
            2 -> Color.parseColor("#B6D7A8")
            3 -> Color.parseColor("#93C47D")
            4 -> Color.parseColor("#6AA84F")
            5 -> Color.parseColor("#38761D")
            else -> Color.WHITE
        }
    }
    
    // Новые функции для анализа
    fun getDetailedForecast(
        fishName: String,
        temperature: Float,
        pressure: Int,
        windSpeed: Float,
        timeOfDay: String,
        season: String,
        moonPhase: String,
        isSpawning: Boolean
    ): DetailedForecast {
        val baseScore = calculateCatchScore(
            temperature, pressure, windSpeed, timeOfDay, season, moonPhase, isSpawning
        )
        
        val fishFactor = getFishFactorDynamic(
            fishName, temperature, pressure, windSpeed, timeOfDay, season, moonPhase, isSpawning
        )
        
        val rawScore = (baseScore * fishFactor).toInt().coerceIn(0, 100)
        val (finalScore, constraintNotes) = applyFishConstraints(
            fishName = fishName,
            temperature = temperature,
            season = season,
            score = rawScore
        )
        val forecastText = getForecastText(finalScore)
        
        return DetailedForecast(
            fishName = fishName,
            baseScore = baseScore,
            fishFactor = fishFactor,
            finalScore = finalScore,
            forecastText = forecastText,
            recommendations = constraintNotes + getRecommendations(fishName, temperature, pressure, windSpeed, timeOfDay, season)
        )
    }

    private fun applyFishConstraints(
        fishName: String,
        temperature: Float,
        season: String,
        score: Int
    ): Pair<Int, List<String>> {
        val profile = fishProfiles[fishName] ?: return score to emptyList()
        var adjustedScore = score
        val notes = mutableListOf<String>()

        if (season == Constants.Season.WINTER && profile.badSeasons.contains(Constants.Season.WINTER)) {
            if (adjustedScore > profile.winterScoreCap) {
                adjustedScore = profile.winterScoreCap
            }
            notes.add("Зимой активность $fishName низкая")
        }

        if (temperature < profile.minTemp) {
            val cap = profile.coldScoreCap
            if (adjustedScore > cap) adjustedScore = cap
            notes.add(
                if (profile.isColdActive) {
                    "Сильный холод снижает активность $fishName, но клев возможен"
                } else {
                    "Температура слишком низкая для $fishName"
                }
            )
        }

        if (temperature > profile.maxTemp) {
            if (adjustedScore > 35) adjustedScore = 35
            notes.add("Температура слишком высокая для $fishName")
        }

        return adjustedScore to notes
    }
    
    private fun getRecommendations(
        fishName: String,
        temperature: Float,
        pressure: Int,
        windSpeed: Float,
        timeOfDay: String,
        season: String
    ): List<String> {
        val recommendations = mutableListOf<String>()
        
        when (fishName) {
            "Окунь" -> {
                if (temperature > 25f) recommendations.add("Температура слишком высокая для окуня")
                if (timeOfDay == Constants.TimeOfDay.MORNING) recommendations.add("Утро - лучшее время для ловли окуня")
                if (season == Constants.Season.SPRING) recommendations.add("Весна - активный период окуня")
            }
            "Щука" -> {
                if (timeOfDay == Constants.TimeOfDay.EVENING) recommendations.add("Вечер - лучшее время для щуки")
                if (temperature in 8f..15f) recommendations.add("Оптимальная температура для щуки")
                if (season == Constants.Season.AUTUMN) recommendations.add("Осень - сезон щуки")
            }
            "Лещ" -> {
                if (pressure in 755..765) recommendations.add("Стабильное давление благоприятно для леща")
                if (temperature in 15f..22f) recommendations.add("Оптимальная температура для леща")
            }
            "Карась" -> {
                if (temperature in 18f..26f) recommendations.add("Теплая вода - идеально для карася")
                if (windSpeed < 5f) recommendations.add("Спокойная вода лучше для карася")
            }
            "Плотва" -> {
                if (season == Constants.Season.AUTUMN) recommendations.add("Осень - лучшее время для плотвы")
                if (timeOfDay == Constants.TimeOfDay.DAY) recommendations.add("День - активное время плотвы")
            }
            "Судак" -> {
                if (timeOfDay == Constants.TimeOfDay.NIGHT) recommendations.add("Ночь - лучшее время для судака")
                if (temperature in 10f..18f) recommendations.add("Прохладная вода - идеально для судака")
            }
            "Сазан" -> {
                if (temperature in 20f..28f) recommendations.add("Теплая вода - идеально для сазана")
                if (windSpeed < 3f) recommendations.add("Спокойная вода лучше для сазана")
            }
        }
        
        return recommendations
    }
    
    data class DetailedForecast(
        val fishName: String,
        val baseScore: Int,
        val fishFactor: Float,
        val finalScore: Int,
        val forecastText: String,
        val recommendations: List<String>
    )
}
