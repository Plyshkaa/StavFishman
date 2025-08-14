package com.example.fishingforecastappstav

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater

import com.example.fishingforecastappstav.mainScreen.FishSelectionAdapter
import com.example.fishingforecastappstav.databinding.ActivityMainBinding
import com.example.fishingforecastappstav.mainScreen.AboutScreen
import com.example.fishingforecastappstav.mainScreen.Fish
import com.example.fishingforecastappstav.mainScreen.FishAdapter
import com.example.fishingforecastappstav.mainScreen.GuideActivity
import com.example.fishingforecastappstav.mainScreen.MapActivity
import com.example.fishingforecastappstav.utils.DateUtils
import com.example.fishingforecastappstav.utils.ForecastUtils
import com.example.fishingforecastappstav.weather.WeatherManager

class MainActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityMainBinding
    
    // Менеджеры
    private lateinit var weatherManager: WeatherManager


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

    // Текущие погодные данные
    private var currentWeatherData: WeatherManager.WeatherData? = null
    private lateinit var fishSelectionDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeManagers()
        setupUI()
        loadInitialData()
    }

    override fun onResume() {
        super.onResume()
        updateDate()
        refreshWeatherData()
    }

    private fun initializeManagers() {
        weatherManager = WeatherManager()

    }

    private fun setupUI() {
        // Современный подход к системным элементам
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        // Скрываем ActionBar
        supportActionBar?.hide()

        // Настраиваем RecyclerView со списком рыб
        setupFishRecyclerView()

        // Кнопка "Выберите рыбу" - открываем диалог для выбора
        binding.btnSelectFish.setOnClickListener {
            showFishSelectionDialog()
        }
        
        // Кнопка сброса выбора рыбы
        binding.btnResetFish.setOnClickListener {
            resetFishSelection()
        }
    }



    private fun loadInitialData() {
        updateDate()
        fetchWeatherData()
    }

    private fun updateDate() {
        binding.tvCurrentDate.text = DateUtils.getCurrentDate()
    }

    private fun setupFishRecyclerView() {
        val adapter = FishAdapter { fish ->
            Log.d("MainActivity", "Нажата рыба: ${fish.name}")
            openFishDetails(fish)
        }
        binding.rvFishList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFishList.adapter = adapter
        adapter.submitList(fishList)
    }

    private fun fetchWeatherData() {
        weatherManager.fetchWeather(object : WeatherManager.WeatherCallback {
            override fun onWeatherLoaded(weatherData: WeatherManager.WeatherData) {
                currentWeatherData = weatherData
                binding.tvWeatherForecast.text = weatherManager.formatWeatherInfo(weatherData)
            }

            override fun onWeatherError(error: String) {
                binding.tvWeatherForecast.text = "Ошибка: $error"
            }
        })
    }

    private fun refreshWeatherData() {
        fetchWeatherData()
    }



    // Кастомный диалог для выбора рыбы
    private fun showFishSelectionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.fish_selection_dialog, null)
        val recyclerView = dialogView.findViewById<RecyclerView>(R.id.rvFishSelection)
        
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = FishSelectionAdapter(fishList) { selectedFish ->
            calculateAndDisplayForecast(selectedFish)
            // Закрываем диалог
            if (::fishSelectionDialog.isInitialized) {
                fishSelectionDialog.dismiss()
            }
        }
        recyclerView.adapter = adapter
        
        fishSelectionDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        
        fishSelectionDialog.show()
    }

    private fun calculateAndDisplayForecast(fish: Fish) {
        currentWeatherData?.let { weatherData ->
            // Используем улучшенный детальный прогноз
            val detailedForecast = ForecastUtils.getDetailedForecast(
                fishName = fish.name,
                temperature = weatherData.temperature,
                pressure = weatherData.pressure,
                windSpeed = weatherData.windSpeed,
                timeOfDay = weatherData.timeOfDay,
                season = weatherData.season,
                moonPhase = weatherData.moonPhase,
                isSpawning = weatherData.isSpawning
            )
            
            // Формируем подробный текст прогноза
            val forecastText = buildString {
                append("🐟 ${fish.name}: ${detailedForecast.forecastText} клев")
                append("\n📊 Балл: ${detailedForecast.finalScore}/100")
                if (detailedForecast.recommendations.isNotEmpty()) {
                    append("\n💡 ${detailedForecast.recommendations.first()}")
                }
            }
            
            binding.tvCatchForecast.text = forecastText
            
            // Показываем кнопку сброса
            binding.btnResetFish.visibility = View.VISIBLE
        } ?: run {
            binding.tvCatchForecast.text = "Загрузка погодных данных..."
        }
    }
    
    private fun resetFishSelection() {
        binding.tvCatchForecast.text = "Прогноз клева для рыбы:"
        binding.btnResetFish.visibility = View.GONE
    }

    // Открытие детального экрана для рыбы
    private fun openFishDetails(fish: Fish) {
        val intent = Intent(this, FishDetailActivity::class.java)
        intent.putExtra("fishName", fish.name)
        startActivity(intent)
    }
}
