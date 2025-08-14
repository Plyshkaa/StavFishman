package com.example.fishingforecastappstav.utils

import android.graphics.Color
import com.example.fishingforecastappstav.utils.Constants

object ForecastUtils {
    
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
        var score = 0
        
        // Температура (0-25 баллов) - более точная шкала
        score += when {
            temp in 15f..22f -> 25  // Оптимальная температура
            temp in 10f..25f -> 20  // Хорошая температура
            temp in 5f..30f -> 10   // Приемлемая температура
            temp in 0f..35f -> 5    // Неблагоприятная температура
            else -> 0               // Экстремальная температура
        }
        
        // Ветер (0-15 баллов) - более детальная шкала
        score += when {
            windSpeed in 2f..6f -> 15   // Оптимальный ветер
            windSpeed in 1f..8f -> 10   // Хороший ветер
            windSpeed in 0f..12f -> 5   // Приемлемый ветер
            else -> 0                   // Сильный ветер
        }
        
        // Давление (0-20 баллов) - более точная шкала
        score += when {
            pressure in 750..770 -> 20  // Оптимальное давление
            pressure in 740..780 -> 15  // Хорошее давление
            pressure in 730..790 -> 10  // Приемлемое давление
            else -> 5                   // Неблагоприятное давление
        }
        
        // Время суток (0-15 баллов) - более точная шкала
        score += when (timeOfDay) {
            Constants.TimeOfDay.MORNING -> 15   // Утро - лучшее время
            Constants.TimeOfDay.EVENING -> 12   // Вечер - хорошее время
            Constants.TimeOfDay.DAY -> 8        // День - среднее время
            Constants.TimeOfDay.NIGHT -> 5      // Ночь - плохое время
            else -> 0
        }
        
        // Сезон (0-15 баллов) - более точная шкала
        score += when (season) {
            Constants.Season.AUTUMN -> 15   // Осень - лучшее время
            Constants.Season.SPRING -> 12   // Весна - хорошее время
            Constants.Season.SUMMER -> 10   // Лето - среднее время
            Constants.Season.WINTER -> 5    // Зима - плохое время
            else -> 0
        }
        
        // Фаза луны (0-10 баллов) - более точная шкала
        score += when (moonPhase) {
            Constants.MoonPhase.NEW_MOON -> 10      // Новолуние - лучшее время
            Constants.MoonPhase.FULL_MOON -> 8      // Полнолуние - хорошее время
            Constants.MoonPhase.FIRST_QUARTER -> 6  // Первая четверть
            Constants.MoonPhase.LAST_QUARTER -> 4   // Последняя четверть
            else -> 0
        }
        
        // Нерест (штраф -20 баллов) - увеличенный штраф
        if (isSpawning) score -= 20
        
        return score.coerceIn(0, 100) // Ограничиваем диапазон [0, 100]
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
                    Constants.Season.WINTER -> dynamicFactor -= 0.1f
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
                    Constants.Season.WINTER -> dynamicFactor -= 0.15f
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
                    Constants.Season.WINTER -> dynamicFactor -= 0.15f
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
                    Constants.Season.WINTER -> dynamicFactor -= 0.1f
                }
            }
            
            "Плотва" -> {
                // Плотва активна осенью и днем
                when (season) {
                    Constants.Season.AUTUMN -> dynamicFactor += 0.2f
                    Constants.Season.SPRING -> dynamicFactor += 0.1f
                    Constants.Season.WINTER -> dynamicFactor -= 0.1f
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
                    Constants.Season.SUMMER -> dynamicFactor -= 0.1f
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
                    Constants.Season.WINTER -> dynamicFactor -= 0.2f
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
        
        val finalScore = (baseScore * fishFactor).toInt().coerceIn(0, 100)
        val forecastText = getForecastText(finalScore)
        
        return DetailedForecast(
            fishName = fishName,
            baseScore = baseScore,
            fishFactor = fishFactor,
            finalScore = finalScore,
            forecastText = forecastText,
            recommendations = getRecommendations(fishName, temperature, pressure, windSpeed, timeOfDay, season)
        )
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
