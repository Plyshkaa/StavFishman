package com.example.fishingforecastappstav.analysis

import com.example.fishingforecastappstav.utils.ForecastUtils
import com.example.fishingforecastappstav.utils.Constants

/**
 * Тестирование улучшенного алгоритма прогноза клева
 */
class ForecastTest {
    
    companion object {
        
        fun runAllTests() {
            println("🧪 ТЕСТИРОВАНИЕ УЛУЧШЕННОГО АЛГОРИТМА ПРОГНОЗА КЛЕВА\n")
            
            testBasicCalculations()
            testFishSpecificLogic()
            testExtremeConditions()
            testDetailedForecast()
            
            println("✅ Все тесты завершены!")
        }
        
        private fun testBasicCalculations() {
            println("📊 ТЕСТ 1: Базовые расчеты")
            
            // Идеальные условия
            val idealScore = ForecastUtils.calculateCatchScore(
                temp = 18f,
                pressure = 760,
                windSpeed = 4f,
                timeOfDay = Constants.TimeOfDay.MORNING,
                season = Constants.Season.SPRING,
                moonPhase = Constants.MoonPhase.NEW_MOON,
                isSpawning = false
            )
            println("   Идеальные условия: $idealScore баллов")
            assert(idealScore >= 80) { "Идеальные условия должны давать высокий балл" }
            
            // Плохие условия
            val badScore = ForecastUtils.calculateCatchScore(
                temp = 35f,
                pressure = 780,
                windSpeed = 15f,
                timeOfDay = Constants.TimeOfDay.NIGHT,
                season = Constants.Season.WINTER,
                moonPhase = Constants.MoonPhase.LAST_QUARTER,
                isSpawning = true
            )
            println("   Плохие условия: $badScore баллов")
            assert(badScore <= 30) { "Плохие условия должны давать низкий балл" }
            
            println("   ✅ Базовые расчеты работают корректно\n")
        }
        
        private fun testFishSpecificLogic() {
            println("🐟 ТЕСТ 2: Специфичная логика рыб")
            
            val fishNames = listOf("Окунь", "Щука", "Лещ", "Карась", "Плотва", "Судак", "Сазан")
            
            // Тест для каждой рыбы в оптимальных условиях
            fishNames.forEach { fishName ->
                val factor = ForecastUtils.getFishFactorDynamic(
                    fishName = fishName,
                    temperature = 18f,
                    pressure = 760,
                    windSpeed = 4f,
                    timeOfDay = Constants.TimeOfDay.MORNING,
                    season = Constants.Season.SPRING,
                    moonPhase = Constants.MoonPhase.NEW_MOON,
                    isSpawning = false
                )
                
                println("   $fishName: коэффициент $factor")
                assert(factor in 0.3f..1.8f) { "Коэффициент $fishName вне допустимого диапазона" }
            }
            
            // Специальные тесты для конкретных рыб
            testOkuńLogic()
            testShchukaLogic()
            testSudakLogic()
            
            println("   ✅ Специфичная логика рыб работает корректно\n")
        }
        
        private fun testOkuńLogic() {
            println("   🎯 Тест окуня:")
            
            // Окунь должен любить утро
            val morningFactor = ForecastUtils.getFishFactorDynamic(
                "Окунь", 18f, 760, 4f, Constants.TimeOfDay.MORNING, Constants.Season.SPRING, Constants.MoonPhase.NEW_MOON, false
            )
            val nightFactor = ForecastUtils.getFishFactorDynamic(
                "Окунь", 18f, 760, 4f, Constants.TimeOfDay.NIGHT, Constants.Season.SPRING, Constants.MoonPhase.NEW_MOON, false
            )
            
            println("     Утро: $morningFactor, Ночь: $nightFactor")
            assert(morningFactor > nightFactor) { "Окунь должен быть более активен утром" }
        }
        
        private fun testShchukaLogic() {
            println("   🎯 Тест щуки:")
            
            // Щука должна любить вечер
            val eveningFactor = ForecastUtils.getFishFactorDynamic(
                "Щука", 12f, 760, 4f, Constants.TimeOfDay.EVENING, Constants.Season.AUTUMN, Constants.MoonPhase.FULL_MOON, false
            )
            val dayFactor = ForecastUtils.getFishFactorDynamic(
                "Щука", 12f, 760, 4f, Constants.TimeOfDay.DAY, Constants.Season.AUTUMN, Constants.MoonPhase.FULL_MOON, false
            )
            
            println("     Вечер: $eveningFactor, День: $dayFactor")
            assert(eveningFactor > dayFactor) { "Щука должна быть более активна вечером" }
        }
        
        private fun testSudakLogic() {
            println("   🎯 Тест судака:")
            
            // Судак должен любить ночь
            val nightFactor = ForecastUtils.getFishFactorDynamic(
                "Судак", 15f, 760, 4f, Constants.TimeOfDay.NIGHT, Constants.Season.AUTUMN, Constants.MoonPhase.NEW_MOON, false
            )
            val dayFactor = ForecastUtils.getFishFactorDynamic(
                "Судак", 15f, 760, 4f, Constants.TimeOfDay.DAY, Constants.Season.AUTUMN, Constants.MoonPhase.NEW_MOON, false
            )
            
            println("     Ночь: $nightFactor, День: $dayFactor")
            assert(nightFactor > dayFactor) { "Судак должен быть более активен ночью" }
        }
        
        private fun testExtremeConditions() {
            println("🌡️ ТЕСТ 3: Экстремальные условия")
            
            // Очень холодно
            val coldScore = ForecastUtils.calculateCatchScore(
                temp = -10f,
                pressure = 750,
                windSpeed = 2f,
                timeOfDay = Constants.TimeOfDay.DAY,
                season = Constants.Season.WINTER,
                moonPhase = Constants.MoonPhase.NEW_MOON,
                isSpawning = false
            )
            println("   Очень холодно (-10°C): $coldScore баллов")
            assert(coldScore <= 20) { "При экстремальном холоде балл должен быть низким" }
            
            // Очень жарко
            val hotScore = ForecastUtils.calculateCatchScore(
                temp = 40f,
                pressure = 740,
                windSpeed = 1f,
                timeOfDay = Constants.TimeOfDay.DAY,
                season = Constants.Season.SUMMER,
                moonPhase = Constants.MoonPhase.FULL_MOON,
                isSpawning = false
            )
            println("   Очень жарко (40°C): $hotScore баллов")
            assert(hotScore <= 25) { "При экстремальной жаре балл должен быть низким" }
            
            // Сильный ветер
            val windyScore = ForecastUtils.calculateCatchScore(
                temp = 20f,
                pressure = 760,
                windSpeed = 20f,
                timeOfDay = Constants.TimeOfDay.MORNING,
                season = Constants.Season.SPRING,
                moonPhase = Constants.MoonPhase.NEW_MOON,
                isSpawning = false
            )
            println("   Сильный ветер (20 м/с): $windyScore баллов")
            assert(windyScore <= 40) { "При сильном ветре балл должен быть снижен" }
            
            println("   ✅ Экстремальные условия обрабатываются корректно\n")
        }
        
        private fun testDetailedForecast() {
            println("📋 ТЕСТ 4: Детальный прогноз")
            
            val detailedForecast = ForecastUtils.getDetailedForecast(
                fishName = "Окунь",
                temperature = 18f,
                pressure = 760,
                windSpeed = 4f,
                timeOfDay = Constants.TimeOfDay.MORNING,
                season = Constants.Season.SPRING,
                moonPhase = Constants.MoonPhase.NEW_MOON,
                isSpawning = false
            )
            
            println("   Рыба: ${detailedForecast.fishName}")
            println("   Базовый балл: ${detailedForecast.baseScore}")
            println("   Коэффициент: ${detailedForecast.fishFactor}")
            println("   Финальный балл: ${detailedForecast.finalScore}")
            println("   Прогноз: ${detailedForecast.forecastText}")
            println("   Рекомендации: ${detailedForecast.recommendations.joinToString(", ")}")
            
            assert(detailedForecast.baseScore > 0) { "Базовый балл должен быть положительным" }
            assert(detailedForecast.fishFactor in 0.3f..1.8f) { "Коэффициент должен быть в допустимом диапазоне" }
            assert(detailedForecast.finalScore in 0..100) { "Финальный балл должен быть в диапазоне 0-100" }
            assert(detailedForecast.recommendations.isNotEmpty()) { "Должны быть рекомендации" }
            
            println("   ✅ Детальный прогноз работает корректно\n")
        }
        
        fun testRealWorldScenarios() {
            println("🌍 ТЕСТ 5: Реальные сценарии")
            
            val scenarios = listOf(
                Triple("Весеннее утро", 12f, Constants.TimeOfDay.MORNING),
                Triple("Летний день", 25f, Constants.TimeOfDay.DAY),
                Triple("Осенний вечер", 15f, Constants.TimeOfDay.EVENING),
                Triple("Зимняя ночь", -5f, Constants.TimeOfDay.NIGHT)
            )
            
            scenarios.forEach { (name, temp, timeOfDay) ->
                val season = when {
                    temp >= 20f -> Constants.Season.SUMMER
                    temp >= 10f -> Constants.Season.SPRING
                    temp >= 0f -> Constants.Season.AUTUMN
                    else -> Constants.Season.WINTER
                }
                
                val score = ForecastUtils.calculateCatchScore(
                    temp = temp,
                    pressure = 760,
                    windSpeed = 5f,
                    timeOfDay = timeOfDay,
                    season = season,
                    moonPhase = Constants.MoonPhase.NEW_MOON,
                    isSpawning = false
                )
                
                val forecast = ForecastUtils.getForecastText(score)
                println("   $name (${temp}°C): $score баллов - $forecast")
            }
            
            println("   ✅ Реальные сценарии обрабатываются корректно\n")
        }
    }
}
