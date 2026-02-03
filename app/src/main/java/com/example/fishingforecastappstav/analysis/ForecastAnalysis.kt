package com.example.fishingforecastappstav.analysis

import com.example.fishingforecastappstav.domain.forecast.ForecastUtils
import com.example.fishingforecastappstav.utils.Constants

/**
 * Анализ и тестирование алгоритма прогноза клева
 */
class ForecastAnalysis {
    
    data class TestCase(
        val name: String,
        val temperature: Float,
        val pressure: Int,
        val windSpeed: Float,
        val timeOfDay: String,
        val season: String,
        val moonPhase: String,
        val isSpawning: Boolean,
        val expectedScore: Int? = null
    )
    
    data class AnalysisResult(
        val testCase: TestCase,
        val baseScore: Int,
        val fishFactors: Map<String, Float>,
        val finalScores: Map<String, Int>,
        val isValid: Boolean,
        val issues: List<String>
    )
    
    companion object {
        
        // Тестовые случаи для проверки алгоритма
        private val testCases = listOf(
            // Идеальные условия
            TestCase(
                name = "Идеальные условия",
                temperature = 18f,
                pressure = 760,
                windSpeed = 4f,
                timeOfDay = Constants.TimeOfDay.MORNING,
                season = Constants.Season.SPRING,
                moonPhase = Constants.MoonPhase.NEW_MOON,
                isSpawning = false,
                expectedScore = 85
            ),
            
            // Хорошие условия
            TestCase(
                name = "Хорошие условия",
                temperature = 22f,
                pressure = 755,
                windSpeed = 6f,
                timeOfDay = Constants.TimeOfDay.EVENING,
                season = Constants.Season.SUMMER,
                moonPhase = Constants.MoonPhase.FULL_MOON,
                isSpawning = false,
                expectedScore = 75
            ),
            
            // Плохие условия
            TestCase(
                name = "Плохие условия",
                temperature = 35f,
                pressure = 780,
                windSpeed = 15f,
                timeOfDay = Constants.TimeOfDay.NIGHT,
                season = Constants.Season.WINTER,
                moonPhase = Constants.MoonPhase.LAST_QUARTER,
                isSpawning = true,
                expectedScore = 15
            ),
            
            // Экстремальные условия
            TestCase(
                name = "Экстремально холодно",
                temperature = -5f,
                pressure = 750,
                windSpeed = 2f,
                timeOfDay = Constants.TimeOfDay.DAY,
                season = Constants.Season.WINTER,
                moonPhase = Constants.MoonPhase.NEW_MOON,
                isSpawning = false,
                expectedScore = 25
            ),
            
            TestCase(
                name = "Экстремально жарко",
                temperature = 40f,
                pressure = 740,
                windSpeed = 1f,
                timeOfDay = Constants.TimeOfDay.DAY,
                season = Constants.Season.SUMMER,
                moonPhase = Constants.MoonPhase.FULL_MOON,
                isSpawning = false,
                expectedScore = 20
            )
        )
        
        fun analyzeAlgorithm(): List<AnalysisResult> {
            val results = mutableListOf<AnalysisResult>()
            
            testCases.forEach { testCase ->
                val baseScore = ForecastUtils.calculateCatchScore(
                    testCase.temperature,
                    testCase.pressure,
                    testCase.windSpeed,
                    testCase.timeOfDay,
                    testCase.season,
                    testCase.moonPhase,
                    testCase.isSpawning
                )
                
                val fishFactors = mutableMapOf<String, Float>()
                val finalScores = mutableMapOf<String, Int>()
                val issues = mutableListOf<String>()
                
                // Проверяем для каждой рыбы
                val fishNames = listOf("Окунь", "Щука", "Лещ", "Карась", "Плотва", "Судак", "Сазан")
                
                fishNames.forEach { fishName ->
                    val fishFactor = ForecastUtils.getFishFactorDynamic(
                        fishName,
                        testCase.temperature,
                        testCase.pressure,
                        testCase.windSpeed,
                        testCase.timeOfDay,
                        testCase.season,
                        testCase.moonPhase,
                        testCase.isSpawning
                    )
                    
                    fishFactors[fishName] = fishFactor
                    val finalScore = (baseScore * fishFactor).toInt()
                    finalScores[fishName] = finalScore
                    
                    // Проверяем логику
                    validateFishLogic(fishName, testCase, fishFactor, issues)
                }
                
                // Проверяем базовую логику
                validateBaseLogic(testCase, baseScore, issues)
                
                val isValid = issues.isEmpty()
                
                results.add(AnalysisResult(
                    testCase = testCase,
                    baseScore = baseScore,
                    fishFactors = fishFactors,
                    finalScores = finalScores,
                    isValid = isValid,
                    issues = issues
                ))
            }
            
            return results
        }
        
        private fun validateBaseLogic(testCase: TestCase, baseScore: Int, issues: MutableList<String>) {
            // Проверяем диапазон баллов
            if (baseScore < 0) {
                issues.add("Базовый балл не может быть отрицательным: $baseScore")
            }
            
            if (baseScore > 100) {
                issues.add("Базовый балл слишком высокий: $baseScore")
            }
            
            // Проверяем логику температуры
            when {
                testCase.temperature in 15f..20f && baseScore < 25 -> {
                    issues.add("При оптимальной температуре (${testCase.temperature}°C) балл слишком низкий: $baseScore")
                }
                testCase.temperature > 30f && baseScore > 30 -> {
                    issues.add("При высокой температуре (${testCase.temperature}°C) балл слишком высокий: $baseScore")
                }
                testCase.temperature < 0f && baseScore > 20 -> {
                    issues.add("При низкой температуре (${testCase.temperature}°C) балл слишком высокий: $baseScore")
                }
            }
            
            // Проверяем логику давления
            if (testCase.pressure in 750..770 && baseScore < 15) {
                issues.add("При оптимальном давлении (${testCase.pressure}) балл слишком низкий: $baseScore")
            }
            
            // Проверяем логику нереста
            if (testCase.isSpawning && baseScore > 50) {
                issues.add("Во время нереста балл слишком высокий: $baseScore")
            }
        }
        
        private fun validateFishLogic(
            fishName: String,
            testCase: TestCase,
            fishFactor: Float,
            issues: MutableList<String>
        ) {
            // Проверяем диапазон коэффициента
            if (fishFactor < 0.5f || fishFactor > 1.5f) {
                issues.add("Коэффициент для $fishName вне допустимого диапазона: $fishFactor")
            }
            
            // Проверяем специфичную логику для каждой рыбы
            when (fishName) {
                "Окунь" -> {
                    if (testCase.temperature > 25f && fishFactor > 1.0f) {
                        issues.add("Окунь при высокой температуре должен иметь пониженный коэффициент")
                    }
                    if (testCase.timeOfDay == Constants.TimeOfDay.MORNING && fishFactor < 1.0f) {
                        issues.add("Окунь утром должен иметь повышенный коэффициент")
                    }
                }
                "Щука" -> {
                    if ((testCase.timeOfDay == Constants.TimeOfDay.EVENING || 
                         testCase.timeOfDay == Constants.TimeOfDay.NIGHT) && fishFactor < 1.2f) {
                        issues.add("Щука в сумерках должна иметь повышенный коэффициент")
                    }
                    if (testCase.temperature < 5f && fishFactor > 1.0f) {
                        issues.add("Щука при низкой температуре должна иметь пониженный коэффициент")
                    }
                }
                "Лещ" -> {
                    if (testCase.season == Constants.Season.WINTER && fishFactor > 0.9f) {
                        issues.add("Лещ зимой должен иметь пониженный коэффициент")
                    }
                    if (testCase.pressure > 760f && fishFactor < 1.0f) {
                        issues.add("Лещ при высоком давлении должен иметь повышенный коэффициент")
                    }
                }
                "Карась" -> {
                    if (testCase.temperature in 18f..25f && fishFactor < 1.0f) {
                        issues.add("Карась при оптимальной температуре должен иметь повышенный коэффициент")
                    }
                    if (testCase.windSpeed > 8f && fishFactor > 1.0f) {
                        issues.add("Карась при сильном ветре должен иметь пониженный коэффициент")
                    }
                }
                "Плотва" -> {
                    if (testCase.season == Constants.Season.AUTUMN && fishFactor < 1.1f) {
                        issues.add("Плотва осенью должна иметь повышенный коэффициент")
                    }
                    if (testCase.timeOfDay == Constants.TimeOfDay.DAY && fishFactor < 1.1f) {
                        issues.add("Плотва днем должна иметь повышенный коэффициент")
                    }
                }
                "Судак" -> {
                    if (testCase.timeOfDay == Constants.TimeOfDay.NIGHT && fishFactor < 1.3f) {
                        issues.add("Судак ночью должен иметь повышенный коэффициент")
                    }
                    if (testCase.temperature < 10f && fishFactor > 1.2f) {
                        issues.add("Судак при низкой температуре должен иметь пониженный коэффициент")
                    }
                }
                "Сазан" -> {
                    if (testCase.temperature in 20f..28f && fishFactor < 1.0f) {
                        issues.add("Сазан при оптимальной температуре должен иметь повышенный коэффициент")
                    }
                    if (testCase.season == Constants.Season.SUMMER && fishFactor < 1.0f) {
                        issues.add("Сазан летом должен иметь повышенный коэффициент")
                    }
                }
            }
        }
        
        fun printAnalysisReport() {
            println("=== АНАЛИЗ АЛГОРИТМА ПРОГНОЗА КЛЕВА ===\n")
            
            val results = analyzeAlgorithm()
            
            results.forEach { result ->
                println("📊 Тест: ${result.testCase.name}")
                println("   Условия: ${result.testCase.temperature}°C, ${result.testCase.pressure} мм рт.ст., ${result.testCase.windSpeed} м/с")
                println("   Время: ${result.testCase.timeOfDay}, Сезон: ${result.testCase.season}")
                println("   Базовый балл: ${result.baseScore}")
                
                println("   Коэффициенты рыб:")
                result.fishFactors.forEach { (fish, factor) ->
                    val finalScore = result.finalScores[fish]!!
                    val forecast = ForecastUtils.getForecastText(finalScore)
                    println("     $fish: $factor → $finalScore ($forecast)")
                }
                
                if (result.issues.isNotEmpty()) {
                    println("   ⚠️ Проблемы:")
                    result.issues.forEach { issue ->
                        println("     - $issue")
                    }
                } else {
                    println("   ✅ Тест пройден успешно")
                }
                println()
            }
            
            val totalIssues = results.sumOf { it.issues.size }
            val passedTests = results.count { it.issues.isEmpty() }
            
            println("=== ИТОГИ АНАЛИЗА ===")
            println("Всего тестов: ${results.size}")
            println("Пройдено успешно: $passedTests")
            println("Найдено проблем: $totalIssues")
            
            if (totalIssues == 0) {
                println("🎉 Алгоритм работает корректно!")
            } else {
                println("🔧 Требуется доработка алгоритма")
            }
        }
    }
}
