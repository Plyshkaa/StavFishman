package com.example.fishingforecastappstav.utils

import com.example.fishingforecastappstav.domain.forecast.ForecastUtils
import org.junit.Assert.assertTrue
import org.junit.Test

class ForecastUtilsTest {

    @Test
    fun calculateCatchScore_idealConditions_isHigh() {
        val score = ForecastUtils.calculateCatchScore(
            temp = 18f,
            pressure = 760,
            windSpeed = 4f,
            timeOfDay = Constants.TimeOfDay.MORNING,
            season = Constants.Season.SPRING,
            moonPhase = Constants.MoonPhase.NEW_MOON,
            isSpawning = false
        )

        assertTrue("Идеальные условия должны давать высокий балл", score >= 80)
    }

    @Test
    fun calculateCatchScore_badConditions_isLow() {
        val score = ForecastUtils.calculateCatchScore(
            temp = 35f,
            pressure = 780,
            windSpeed = 15f,
            timeOfDay = Constants.TimeOfDay.NIGHT,
            season = Constants.Season.WINTER,
            moonPhase = Constants.MoonPhase.LAST_QUARTER,
            isSpawning = true
        )

        assertTrue("Плохие условия должны давать низкий балл", score <= 30)
    }

    @Test
    fun getDetailedForecast_returnsValidScore() {
        val result = ForecastUtils.getDetailedForecast(
            fishName = "Окунь",
            temperature = 18f,
            pressure = 760,
            windSpeed = 4f,
            timeOfDay = Constants.TimeOfDay.MORNING,
            season = Constants.Season.SPRING,
            moonPhase = Constants.MoonPhase.NEW_MOON,
            isSpawning = false
        )

        assertTrue("Финальный балл должен быть в диапазоне 0..100", result.finalScore in 0..100)
        assertTrue("Рекомендации должны быть не пустыми", result.recommendations.isNotEmpty())
    }
}
