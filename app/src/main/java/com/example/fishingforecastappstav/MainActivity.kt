package com.example.fishingforecastappstav

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fishingforecastappstav.databinding.ActivityMainBinding
import com.example.fishingforecastappstav.mainScreen.AboutScreen
import com.example.fishingforecastappstav.mainScreen.Fish
import com.example.fishingforecastappstav.mainScreen.FishAdapter
import com.example.fishingforecastappstav.mainScreen.GuideActivity
import com.example.fishingforecastappstav.mainScreen.MapActivity
import com.example.fishingforecastappstav.mainScreen.RetrofitInstance
import com.example.fishingforecastappstav.mainScreen.WeatherResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityMainBinding

    // Статический список рыб (RecyclerView)
    private val fishList = listOf(
        Fish("Окунь", R.drawable.ic_okun1, "Лучший клев в утренние часы."),
        Fish("Щука", R.drawable.ic_pike, "Хищник, клюёт в сумерки."),
        Fish("Лещ", R.drawable.ic_bream, "Питается в пасмурные дни."),
        Fish("Карась", R.drawable.ic_crucian_carp, "Часто разводится в прудах, устойчив даже к неблагоприятным условиям."),
        Fish("Плотва", R.drawable.ic_roach, "Активно кормится в пресной воде, особенно в осенний период."),
        Fish("Судак", R.drawable.ic_zander, "Ценится за спортивную ловлю, встречается в чистых реках и озёрах."),
        Fish("Сазан", R.drawable.ic_carp, "Крупная рыба, часто разводимая в водохранилищах и прудах.")
    )

    // Data class для календаря клева
    data class FishCalendarData(
        val fishName: String,
        val monthlyActivity: List<Int> // 12 значений (0..5)
    )

    // Массив названий месяцев
    private val months = arrayOf("Янв", "Фев", "Мар", "Апр", "Май", "Июн", "Июл", "Авг", "Сен", "Окт", "Ноя", "Дек")

    // Пример списка данных для календаря клева
    private val fishCalendarList = listOf(
        FishCalendarData("Карп", listOf(1, 2, 3, 4, 5, 5, 4, 4, 3, 2, 1, 1)),
        FishCalendarData("Карась", listOf(1, 2, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1)),
        FishCalendarData("Белый амур", listOf(1, 1, 2, 3, 4, 5, 5, 4, 3, 2, 1, 1)),
        FishCalendarData("Лещ", listOf(0, 1, 2, 3, 4, 5, 4, 4, 3, 2, 1, 0))
    )

    // Погодные данные (обновляются после fetchWeather)
    private var currentTemperature: Float = 0f
    private var currentPressure: Int = 0
    private var currentWindSpeed: Float = 0f
    private var currentTimeOfDay: String = "Morning"
    private var currentSeason: String = "Spring"
    private var currentMoonPhase: String = "New Moon"
    private var currentIsSpawning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Прозрачный статус-бар и тёмные иконки
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                )
        window.statusBarColor = Color.TRANSPARENT

        // Настраиваем Toolbar как ActionBar (если нужно)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        // Настраиваем нижнее меню
        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_map -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutScreen::class.java))
                    true
                }
                else -> false
            }
        }

        // Устанавливаем текущую дату
        binding.tvCurrentDate.text = getCurrentDate()

        // Настраиваем RecyclerView со списком рыб
        setupFishRecyclerView()

        // Заполняем календарь клева
        showCalendar()

        // Кнопка "Выберите рыбу" - открываем PopupMenu для выбора
        binding.btnSelectFish.setOnClickListener {
            showFishPopupMenu()
        }

        // Загружаем данные о погоде
        fetchWeather()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем дату
        binding.tvCurrentDate.text = getCurrentDate()
        // Обновляем погодные данные
        fetchWeather()
        // При необходимости обновляем календарь
        showCalendar()
    }

    // PopupMenu для выбора рыбы
    private fun showFishPopupMenu() {
        val popup = PopupMenu(this, binding.btnSelectFish)
        // Заполняем меню названиями рыб
        fishList.forEach { fish ->
            popup.menu.add(fish.name)
        }
        popup.setOnMenuItemClickListener { menuItem ->
            // Находим выбранную рыбу по имени
            val selectedFish = fishList.find { it.name == menuItem.title.toString() }
            if (selectedFish != null) {
                // Рассчитываем динамический коэффициент
                val fishFactor = getFishFactorDynamic(
                    fishName = selectedFish.name,
                    temperature = currentTemperature,
                    pressure = currentPressure,
                    windSpeed = currentWindSpeed,
                    timeOfDay = currentTimeOfDay,
                    season = currentSeason,
                    moonPhase = currentMoonPhase,
                    isSpawning = currentIsSpawning
                )
                // Базовый расчёт
                val baseScore = calculateCatchScore(
                    currentTemperature,
                    currentPressure,
                    currentWindSpeed,
                    currentTimeOfDay,
                    currentSeason,
                    currentMoonPhase,
                    currentIsSpawning
                )
                val finalScore = (baseScore * fishFactor).toInt()
                val forecastText = getForecastText(finalScore)
                binding.tvCatchForecast.text = "Прогноз клева для рыбы ${selectedFish.name}: $forecastText"

                // Подсветка блока (нежные цвета)
                val color = getForecastColor(forecastText)
                binding.tvCatchForecast.setBackgroundColor(color)
            }
            true
        }
        popup.show()
    }

    // Функция динамического расчёта fishFactor
    private fun getFishFactorDynamic(
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
            "Щука" -> 1.2f
            "Лещ" -> 0.9f
            "Карась" -> 1.0f
            "Плотва" -> 1.1f
            "Судак" -> 1.3f
            "Сазан" -> 1.0f
            else -> 1.0f
        }
        var dynamicFactor = baseFactor

        // Пример корректировок
        if (fishName == "Окунь" && temperature > 25) {
            dynamicFactor -= 0.1f
        }
        if (fishName == "Щука" && (timeOfDay == "Evening" || timeOfDay == "Night")) {
            dynamicFactor += 0.1f
        }
        if (fishName == "Лещ" && season == "Winter") {
            dynamicFactor -= 0.1f
        }
        if (isSpawning) {
            dynamicFactor -= 0.2f
        }

        // Ограничиваем диапазон [0.5, 1.5]
        if (dynamicFactor < 0.5f) dynamicFactor = 0.5f
        if (dynamicFactor > 1.5f) dynamicFactor = 1.5f

        return dynamicFactor
    }

    // Настройка RecyclerView
    private fun setupFishRecyclerView() {
        val adapter = FishAdapter(fishList) { fish ->
            Log.d("MainActivity", "Нажата рыба: ${fish.name}")
            openFishDetails(fish)
        }
        binding.rvFishList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFishList.adapter = adapter
    }

    // Загрузка погоды
    private fun fetchWeather() {
        val apiKey = "b2f3cbb0c3c8e31e7a784ac5c9b417c7" // Замените на свой API-ключ
        RetrofitInstance.api.getCurrentWeather("Stavropol", apiKey)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                    if (response.isSuccessful) {
                        val weatherData = response.body()
                        weatherData?.let {
                            // Обновляем погодные данные
                            currentTemperature = it.main.temp
                            currentPressure = it.main.pressure
                            currentWindSpeed = 3.5f
                            currentTimeOfDay = getCurrentTimeOfDay()
                            currentSeason = getSeason()
                            currentMoonPhase = "New Moon"
                            currentIsSpawning = false

                            val timeOfDayRu = getTimeOfDayInRussian(currentTimeOfDay)
                            val seasonRu = getSeasonInRussian(currentSeason)
                            val moonPhaseRu = getMoonPhaseInRussian(currentMoonPhase)
                            val spawningRu = getSpawningInRussian(currentIsSpawning)

                            val infoBuilder = StringBuilder().apply {
                                appendLine("Погода и условия для расчёта:")
                                appendLine("• Температура: $currentTemperature°C")
                                appendLine("• Давление: $currentPressure мм рт. ст.")
                                appendLine("• Ветер: $currentWindSpeed м/с")
                                appendLine("• Время суток: $timeOfDayRu")
                                appendLine("• Сезон: $seasonRu")
                                appendLine("• Фаза луны: $moonPhaseRu")
                                appendLine("• Нерест: $spawningRu")
                            }
                            binding.tvWeatherForecast.text = infoBuilder.toString()
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

    // Определение сезона
    private fun getSeason(): String {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        return when (month) {
            in 3..5 -> "Spring"
            in 6..8 -> "Summer"
            in 9..11 -> "Autumn"
            else -> "Winter"
        }
    }

    // Базовый расчёт баллов клева
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
        // Температура
        score += when {
            temp in 15f..20f -> 30
            temp in 10f..25f -> 20
            else -> 5
        }
        // Ветер
        score += when {
            windSpeed in 3f..5f -> 10
            windSpeed in 1f..10f -> 5
            else -> 0
        }
        // Давление
        score += if (pressure in 750..770) 20 else 10
        // Время суток
        score += when (timeOfDay) {
            "Morning", "Evening" -> 10
            "Day" -> 5
            "Night" -> 3
            else -> 0
        }
        // Сезон
        score += when (season) {
            "Spring" -> 10
            "Summer" -> 15
            "Autumn" -> 20
            "Winter" -> 5
            else -> 0
        }
        // Фаза луны
        score += when (moonPhase) {
            "New Moon", "Full Moon" -> 10
            "First Quarter", "Last Quarter" -> 5
            else -> 0
        }
        // Нерест
        if (isSpawning) score -= 15

        return score
    }

    // Преобразование баллов в текст
    private fun getForecastText(score: Int): String {
        return when {
            score >= 80 -> "Отличный"
            score in 50 until 80 -> "Хороший"
            else -> "Плохой"
        }
    }

    // Время суток (en → ru)
    private fun getTimeOfDayInRussian(timeOfDay: String): String {
        return when (timeOfDay) {
            "Morning" -> "Утро"
            "Day" -> "День"
            "Evening" -> "Вечер"
            "Night" -> "Ночь"
            else -> timeOfDay
        }
    }

    // Сезон (en → ru)
    private fun getSeasonInRussian(season: String): String {
        return when (season) {
            "Spring" -> "Весна"
            "Summer" -> "Лето"
            "Autumn" -> "Осень"
            "Winter" -> "Зима"
            else -> season
        }
    }

    // Фаза луны (en → ru)
    private fun getMoonPhaseInRussian(moonPhase: String): String {
        return when (moonPhase) {
            "New Moon" -> "Новолуние"
            "Full Moon" -> "Полнолуние"
            "First Quarter" -> "Первая четверть"
            "Last Quarter" -> "Последняя четверть"
            else -> moonPhase
        }
    }

    // Нерест (en → ru)
    private fun getSpawningInRussian(isSpawning: Boolean): String {
        return if (isSpawning) "Да" else "Нет"
    }

    // Заполнение календаря клева
    private fun showCalendar() {
        val tableLayout = binding.tlCalendar
        tableLayout.removeAllViews()

        // Заголовок
        val headerRow = TableRow(this)
        val fishHeaderCell = TextView(this).apply {
            text = "Рыба"
            textSize = 14f
            setPadding(16, 16, 16, 16)
        }
        headerRow.addView(fishHeaderCell)

        for (month in months) {
            val monthCell = TextView(this).apply {
                text = month
                textSize = 14f
                setPadding(16, 16, 16, 16)
            }
            headerRow.addView(monthCell)
        }
        tableLayout.addView(headerRow)

        // Данные
        for (fishData in fishCalendarList) {
            val row = TableRow(this)
            val fishNameCell = TextView(this).apply {
                text = fishData.fishName
                textSize = 14f
                setPadding(16, 16, 16, 16)
            }
            row.addView(fishNameCell)

            for (activityValue in fishData.monthlyActivity) {
                val cell = TextView(this).apply {
                    text = activityValue.toString()
                    textSize = 14f
                    setPadding(16, 16, 16, 16)
                    gravity = android.view.Gravity.CENTER
                    setBackgroundColor(getColorForActivity(activityValue))
                }
                row.addView(cell)
            }
            tableLayout.addView(row)
        }
    }

    // Цвет ячеек
    private fun getColorForActivity(activity: Int): Int {
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

    // Текущая дата
    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
        return sdf.format(Date())
    }

    // Цвет для "Плохой", "Хороший", "Отличный"
    private fun getForecastColor(forecastText: String): Int {
        return when (forecastText) {
            "Плохой" -> Color.parseColor("#F49292")   // нежно-красный
            "Хороший" -> Color.parseColor("#FFE599") // нежно-жёлтый
            "Отличный" -> Color.parseColor("#B6D7A8")// нежно-зелёный
            else -> Color.WHITE
        }
    }

    private fun getCurrentTimeOfDay(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Morning"    // с 5 до 11 утра
            in 12..16 -> "Day"       // с 12 до 16 дня
            in 17..20 -> "Evening"   // с 17 до 20 вечера
            else -> "Night"          // с 21 до 4 ночи
        }
    }

    // Открытие детального экрана для рыбы
    private fun openFishDetails(fish: Fish) {
        val intent = Intent(this, FishDetailActivity::class.java)
        // Передаём только имя рыбы
        intent.putExtra("fishName", fish.name)
        startActivity(intent)
    }
}
