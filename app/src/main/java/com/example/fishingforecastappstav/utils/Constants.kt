package com.example.fishingforecastappstav.utils

object Constants {
    
    // Open-Meteo base URL (no API key required for free tier)
    const val WEATHER_BASE_URL = "https://api.open-meteo.com/"
    
    // Координаты по умолчанию (Ставрополь)
    // ~45°03′N, 41°58′E
    const val DEFAULT_LATITUDE = 45.05
    const val DEFAULT_LONGITUDE = 41.9667

    // Карта рыболовных мест
    const val MAP_URL = "https://www.google.ru/maps/d/viewer?mid=1WQ1s7X6JYl1-RUIHFGLuv0gsQyJmt8E&ll=44.61720454287925%2C42.77709834377079&z=9"
    
    // Названия месяцев для календаря
    val MONTHS = arrayOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")
    
    // Времена суток
    object TimeOfDay {
        const val MORNING = "Morning"
        const val DAY = "Day"
        const val EVENING = "Evening"
        const val NIGHT = "Night"
    }
    
    // Сезоны
    object Season {
        const val SPRING = "Spring"
        const val SUMMER = "Summer"
        const val AUTUMN = "Autumn"
        const val WINTER = "Winter"
    }
    
    // Фазы луны
    object MoonPhase {
        const val NEW_MOON = "New Moon"
        const val FULL_MOON = "Full Moon"
        const val FIRST_QUARTER = "First Quarter"
        const val LAST_QUARTER = "Last Quarter"
    }
    
    // Прогнозы клева
    object Forecast {
        const val EXCELLENT = "Отличный"
        const val GOOD = "Хороший"
        const val POOR = "Плохой"
    }
    
    // Пороговые значения для прогноза
    object ForecastThresholds {
        const val EXCELLENT_MIN = 80
        const val GOOD_MIN = 50
    }
    
    // Температурные диапазоны
    object TemperatureRanges {
        const val OPTIMAL_MIN = 15f
        const val OPTIMAL_MAX = 20f
        const val GOOD_MIN = 10f
        const val GOOD_MAX = 25f
    }
    
    // Давление
    object PressureRanges {
        // Значения в мм рт. ст.
        const val OPTIMAL_MIN = 750
        const val OPTIMAL_MAX = 770
    }
    
    // Ветер
    object WindRanges {
        const val OPTIMAL_MIN = 3f
        const val OPTIMAL_MAX = 5f
        const val GOOD_MAX = 10f
    }
}
