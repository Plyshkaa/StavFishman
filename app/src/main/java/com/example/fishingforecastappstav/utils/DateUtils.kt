package com.example.fishingforecastappstav.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
        return sdf.format(Date())
    }
    
    fun getCurrentTimeOfDay(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Morning"
            in 12..16 -> "Day"
            in 17..20 -> "Evening"
            else -> "Night"
        }
    }
    
    fun getSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        return when (month) {
            in 3..5 -> "Spring"
            in 6..8 -> "Summer"
            in 9..11 -> "Autumn"
            else -> "Winter"
        }
    }
    
    fun getTimeOfDayInRussian(timeOfDay: String): String {
        return when (timeOfDay) {
            "Morning" -> "Утро"
            "Day" -> "День"
            "Evening" -> "Вечер"
            "Night" -> "Ночь"
            else -> timeOfDay
        }
    }
    
    fun getSeasonInRussian(season: String): String {
        return when (season) {
            "Spring" -> "Весна"
            "Summer" -> "Лето"
            "Autumn" -> "Осень"
            "Winter" -> "Зима"
            else -> season
        }
    }
    
    fun getMoonPhaseInRussian(moonPhase: String): String {
        return when (moonPhase) {
            "New Moon" -> "Новолуние"
            "Full Moon" -> "Полнолуние"
            "First Quarter" -> "Первая четверть"
            "Last Quarter" -> "Последняя четверть"
            else -> moonPhase
        }
    }
    
    fun getSpawningInRussian(isSpawning: Boolean): String {
        return if (isSpawning) "Да" else "Нет"
    }
}
