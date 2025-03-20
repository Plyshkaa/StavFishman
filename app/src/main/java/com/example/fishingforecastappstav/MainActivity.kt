package com.example.fishingforecastappstav

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fishingforecastappstav.databinding.ActivityMainBinding
import com.example.fishingforecastappstav.mainScreen.FishAdapter
import com.example.fishingforecastappstav.mainScreen.Fish
import com.example.fishingforecastappstav.mainScreen.RetrofitInstance
import com.example.fishingforecastappstav.mainScreen.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // Используем View Binding для удобного доступа к элементам layout
    private lateinit var binding: ActivityMainBinding

    // Статический список рыб для отображения (пока статический, можно расширить позже)
    private val fishList = listOf(
        Fish("Окунь", R.drawable.ic_perch, "Лучший клев в утренние часы."),
        Fish("Щука", R.drawable.ic_pike, "Хищник, клюёт в сумерки."),
        Fish("Лещ", R.drawable.ic_bream, "Питается в пасмурные дни.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализируем binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Устанавливаем текущую дату на экране
        binding.tvCurrentDate.text = getCurrentDate()

        // Настраиваем RecyclerView для отображения списка рыб
        setupFishRecyclerView()

        // Получаем данные о погоде и на их основе рассчитываем прогноз клева
        fetchWeather()
    }

    // Функция для получения текущей даты в формате "день месяц год"
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
        return sdf.format(Date())
    }

    // Настройка RecyclerView с горизонтальным расположением элементов
    private fun setupFishRecyclerView() {
        val adapter = FishAdapter(fishList) { fish ->
            // Обработка клика по элементу списка (пока просто лог, потом можно открыть детальную информацию)
            Log.d("MainActivity", "Нажата рыба: ${fish.name}")
            openFishDetails(fish)
        }
        binding.rvFishList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFishList.adapter = adapter
    }

    // Получаем данные о погоде
    private fun fetchWeather() {
        val apiKey = "b2f3cbb0c3c8e31e7a784ac5c9b417c7" // Замените на свой API-ключ
        RetrofitInstance.api.getCurrentWeather("Stavropol", apiKey)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        weatherData?.let {
                            // Получаем данные для расчёта
                            val temperature = it.main.temp
                            val pressure = it.main.pressure
                            val windSpeed = 3.5f
                            // Здесь вы можете динамически определить время суток, сезон и фазу луны
                            val timeOfDay = "Morning"
                            val season = getSeason()
                            val moonPhase = "New Moon"
                            val isSpawning = false

                            // Переводим на русский язык
                            val timeOfDayRu = getTimeOfDayInRussian(timeOfDay)
                            val seasonRu = getSeasonInRussian(season)
                            val moonPhaseRu = getMoonPhaseInRussian(moonPhase)
                            val spawningRu = getSpawningInRussian(isSpawning)

                            // Формируем текст с параметрами
                            val infoBuilder = StringBuilder().apply {
                                appendLine("Погода и условия для расчёта:")
                                appendLine("• Температура: $temperature°C")
                                appendLine("• Давление: $pressure мм рт. ст.")
                                appendLine("• Ветер: $windSpeed м/с")
                                appendLine("• Время суток: $timeOfDayRu")
                                appendLine("• Сезон: $seasonRu")
                                appendLine("• Фаза луны: $moonPhaseRu")
                                appendLine("• Нерест: $spawningRu")
                            }
                            binding.tvWeatherForecast.text = infoBuilder.toString()

                            // Считаем баллы клева
                            val score = calculateCatchScore(
                                temperature,
                                pressure,
                                windSpeed,
                                timeOfDay,  // Важно передать оригинальные англ. значения в функцию
                                season,
                                moonPhase,
                                isSpawning
                            )
                            val forecastText = getForecastText(score)
                            binding.tvCatchForecast.text = "Прогноз клева: $forecastText"
                        }
                    } else {
                        binding.tvWeatherForecast.text = "Ошибка загрузки погоды"
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    binding.tvWeatherForecast.text = "Ошибка: ${t.message}"
                }
            })
    }

    // Функция для определения сезона по текущему месяцу
    private fun getSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1 // месяцы от 0 до 11, поэтому +1
        return when (month) {
            in 3..5 -> "Spring"
            in 6..8 -> "Summer"
            in 9..11 -> "Autumn"
            else -> "Winter"
        }
    }

    /**
     * Функция расчёта баллов клева с учётом разных факторов:
     * @param temp Температура (°C)
     * @param pressure Атмосферное давление (мм рт. ст.)
     * @param windSpeed Скорость ветра (м/с)
     * @param timeOfDay Время суток ("Morning", "Day", "Evening", "Night")
     * @param season Сезон ("Spring", "Summer", "Autumn", "Winter")
     * @param moonPhase Фаза Луны ("New Moon", "Full Moon", "First Quarter", "Last Quarter", "Other")
     * @param isSpawning Флаг нереста (true, если идёт нерест)
     * @return Общий балл клева
     */
    private fun calculateCatchScore(
        temp: Float,
        pressure: Int,
        windSpeed: Float,
        timeOfDay: String,
        season: String,
        moonPhase: String,
        isSpawning: Boolean
    ): Int {
        var score = 0

        // Фактор погоды: оптимальная температура 15-20°C
        score += when {
            temp in 15f..20f -> 30
            temp in 10f..25f -> 20
            else -> 5
        }
        // Учитываем ветер (оптимальный диапазон: 3-5 м/с)
        score += when {
            windSpeed in 3f..5f -> 10
            windSpeed in 1f..10f -> 5
            else -> 0
        }
        // Фактор давления: оптимальное давление 750-770 мм рт. ст.
        score += if (pressure in 750..770) 20 else 10
        // Фактор времени суток
        score += when (timeOfDay) {
            "Morning", "Evening" -> 10
            "Day" -> 5
            "Night" -> 3
            else -> 0
        }
        // Сезонный фактор
        score += when (season) {
            "Spring" -> 10
            "Summer" -> 15
            "Autumn" -> 20
            "Winter" -> 5
            else -> 0
        }
        // Фактор фазы Луны
        score += when (moonPhase) {
            "New Moon", "Full Moon" -> 10
            "First Quarter", "Last Quarter" -> 5
            else -> 0
        }
        // Фактор нереста: если идёт нерест, снижаем балл
        if (isSpawning) score -= 15

        return score
    }

    // Функция для преобразования баллов в текстовый прогноз
    private fun getForecastText(score: Int): String {
        return when {
            score >= 80 -> "Отличный"
            score in 50 until 80 -> "Хороший"
            else -> "Плохой"
        }
    }

    // Функция для обработки клика по элементу списка рыб
    private fun openFishDetails(fish: Fish) {
        // Здесь можно реализовать переход на новую Activity с подробной информацией о рыбе.
        // Пример:
        // val intent = Intent(this, FishDetailActivity::class.java)
        // intent.putExtra("fish", fish)
        // startActivity(intent)
    }

    // Перевод времени суток на русский
    private fun getTimeOfDayInRussian(timeOfDay: String): String {
        return when (timeOfDay) {
            "Morning" -> "Утро"
            "Day" -> "День"
            "Evening" -> "Вечер"
            "Night" -> "Ночь"
            else -> timeOfDay
        }
    }

    // Перевод сезона на русский
    private fun getSeasonInRussian(season: String): String {
        return when (season) {
            "Spring" -> "Весна"
            "Summer" -> "Лето"
            "Autumn" -> "Осень"
            "Winter" -> "Зима"
            else -> season
        }
    }

    // Перевод фазы луны на русский
    private fun getMoonPhaseInRussian(moonPhase: String): String {
        return when (moonPhase) {
            "New Moon" -> "Новолуние"
            "Full Moon" -> "Полнолуние"
            "First Quarter" -> "Первая четверть"
            "Last Quarter" -> "Последняя четверть"
            else -> moonPhase
        }
    }

    // Перевод флага нереста на русский
    private fun getSpawningInRussian(isSpawning: Boolean): String {
        return if (isSpawning) "Да" else "Нет"
    }

}
