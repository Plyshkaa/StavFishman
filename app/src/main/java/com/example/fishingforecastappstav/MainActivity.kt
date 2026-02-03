package com.example.fishingforecastappstav

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.net.Uri
import android.os.Build
import android.content.pm.PackageManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

import androidx.lifecycle.ViewModelProvider
import com.example.fishingforecastappstav.data.repository.WeatherRepositoryImpl
import com.example.fishingforecastappstav.data.local.FavoritesStore
import com.example.fishingforecastappstav.domain.model.WeatherData
import com.example.fishingforecastappstav.domain.usecase.GetWeatherUseCase
import com.example.fishingforecastappstav.mainScreen.FishSelectionAdapter
import com.example.fishingforecastappstav.databinding.ActivityMainBinding
import com.example.fishingforecastappstav.mainScreen.Fish
import com.example.fishingforecastappstav.mainScreen.FishAdapter
import com.example.fishingforecastappstav.notifications.FavoriteForecastWorker
import com.example.fishingforecastappstav.ui.main.MainViewModel
import com.example.fishingforecastappstav.ui.main.MainViewModelFactory
import com.example.fishingforecastappstav.ui.main.WeatherState
import com.example.fishingforecastappstav.utils.Constants
import com.example.fishingforecastappstav.utils.DateUtils

class MainActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityMainBinding
    
    private lateinit var viewModel: MainViewModel
    private lateinit var favoritesStore: FavoritesStore
    private lateinit var favoritesAdapter: FishAdapter
    private lateinit var allFishAdapter: FishAdapter


    // Статический список рыб (RecyclerView)
    private val fishList = listOf(
        Fish("Окунь", R.drawable.ic_perch, "Лучший клев в утренние часы."),
        Fish("Щука", R.drawable.ic_pike, "Хищник, клюёт в сумерки."),
        Fish("Лещ", R.drawable.ic_bream, "Питается в пасмурные дни."),
        Fish("Карась", R.drawable.ic_crucian_carp, "Часто разводится в прудах, устойчив даже к неблагоприятным условиям."),
        Fish("Плотва", R.drawable.ic_roach, "Активно кормится в пресной воде, особенно в осенний период."),
        Fish("Судак", R.drawable.ic_zander, "Ценится за спортивную ловлю, встречается в чистых реках и озёрах."),
        Fish("Сазан", R.drawable.ic_carp, "Крупная рыба, часто разводимая в водохранилищах и прудах.")
    )

    private lateinit var fishSelectionDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViewModel()
        favoritesStore = FavoritesStore(this)
        setupUI()
        loadInitialData()
        setupNotifications()
    }

    override fun onResume() {
        super.onResume()
        updateDate()
        refreshWeatherData()
    }

    private fun initializeViewModel() {
        val repository = WeatherRepositoryImpl()
        val useCase = GetWeatherUseCase(repository)
        val factory = MainViewModelFactory(useCase)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        viewModel.weatherState.observe(this) { state ->
            when (state) {
                is WeatherState.Loading -> {
                    binding.tvWeatherForecast.text = "Загрузка погодных данных..."
                }
                is WeatherState.Success -> {
                    binding.tvWeatherForecast.text = formatWeatherInfo(state.data)
                }
                is WeatherState.Error -> {
                    binding.tvWeatherForecast.text = "Ошибка: ${state.message}"
                }
            }
        }
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

        // Карта рыболовных мест
        binding.btnOpenMap.setOnClickListener {
            openMap()
        }
        
        // Кнопка сброса выбора рыбы
        binding.btnResetFish.setOnClickListener {
            resetFishSelection()
        }

        setupBottomNav()
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_info -> {
                    startActivity(Intent(this, com.example.fishingforecastappstav.mainScreen.InfoActivity::class.java))
                    true
                }
                R.id.nav_map -> {
                    openMap()
                    true
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, com.example.fishingforecastappstav.mainScreen.AboutScreen::class.java))
                    true
                }
                else -> false
            }
        }
    }



    private fun loadInitialData() {
        updateDate()
        viewModel.loadWeather(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE)
    }

    private fun updateDate() {
        binding.tvCurrentDate.text = DateUtils.getCurrentDate()
    }

    private fun setupFishRecyclerView() {
        allFishAdapter = FishAdapter(
            onFishClick = { fish ->
            Log.d("MainActivity", "Нажата рыба: ${fish.name}")
            openFishDetails(fish)
            },
            isFavorite = { fish -> favoritesStore.isFavorite(fish.name) },
            onFavoriteToggle = { fish ->
                favoritesStore.toggleFavorite(fish.name)
                updateFavoritesSection()
            }
        )
        binding.rvFishList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFishList.adapter = allFishAdapter
        allFishAdapter.submitList(fishList)

        favoritesAdapter = FishAdapter(
            onFishClick = { fish -> openFishDetails(fish) },
            isFavorite = { fish -> true },
            onFavoriteToggle = { fish ->
                favoritesStore.toggleFavorite(fish.name)
                updateFavoritesSection()
            }
        )
        binding.rvFavorites.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvFavorites.adapter = favoritesAdapter
        updateFavoritesSection()
    }

    private fun refreshWeatherData() {
        viewModel.loadWeather(Constants.DEFAULT_LATITUDE, Constants.DEFAULT_LONGITUDE)
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
        val detailedForecast = viewModel.calculateForecast(fish)
        if (detailedForecast != null) {
            
            // Формируем подробный текст прогноза
            val forecastText = buildString {
                append("🐟 ${fish.name}: ${detailedForecast.forecastText} клев")
                append("\n📊 Балл: ${detailedForecast.finalScore}/100")
            }
            
            binding.tvCatchForecast.text = forecastText
            binding.tvForecastFactors.text = buildString {
                if (detailedForecast.recommendations.isEmpty()) {
                    append("Факторы: без дополнительных замечаний")
                } else {
                    append("Факторы:\n")
                    detailedForecast.recommendations.forEach { note ->
                        append("• ").append(note).append('\n')
                    }
                }
            }.trim()
            
            // Показываем кнопку сброса
            binding.btnResetFish.visibility = View.VISIBLE
        } else {
            binding.tvCatchForecast.text = "Загрузка погодных данных..."
            binding.tvForecastFactors.text = "Факторы: данные ещё загружаются"
        }
    }
    
    private fun resetFishSelection() {
        binding.tvCatchForecast.text = "Прогноз клева для рыбы:"
        binding.btnResetFish.visibility = View.GONE
        binding.tvForecastFactors.text = "Факторы прогноза появятся после выбора рыбы"
    }

    private fun openMap() {
        val uri = Uri.parse(Constants.MAP_URL)
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, uri)
    }

    private fun updateFavoritesSection() {
        val favorites = favoritesStore.getFavorites()
        val favoriteFish = fishList.filter { favorites.contains(it.name) }
        if (favoriteFish.isEmpty()) {
            binding.tvFavoritesTitle.visibility = View.GONE
            binding.rvFavorites.visibility = View.GONE
        } else {
            binding.tvFavoritesTitle.visibility = View.VISIBLE
            binding.rvFavorites.visibility = View.VISIBLE
            favoritesAdapter.submitList(favoriteFish)
        }
        allFishAdapter.notifyDataSetChanged()
    }

    private fun setupNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATIONS
                )
            } else {
                scheduleFavoriteNotifications()
            }
        } else {
            scheduleFavoriteNotifications()
        }
    }

    private fun scheduleFavoriteNotifications() {
        val delayMillis = calculateInitialDelayMillis(7, 0)
        val request = PeriodicWorkRequestBuilder<FavoriteForecastWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "favorite_forecast",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun calculateInitialDelayMillis(targetHour: Int, targetMinute: Int): Long {
        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (next.before(now)) {
            next.add(Calendar.DAY_OF_YEAR, 1)
        }
        return next.timeInMillis - now.timeInMillis
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATIONS && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            scheduleFavoriteNotifications()
        }
    }

    companion object {
        private const val REQUEST_NOTIFICATIONS = 2001
    }

    private fun formatWeatherInfo(weatherData: WeatherData): String {
        return StringBuilder().apply {
            appendLine("🌤️ Погода и условия для расчёта:")
            appendLine("• Температура: ${weatherData.temperature}°C")
            appendLine("• Давление: ${weatherData.pressureMmHg} мм рт. ст.")
            appendLine("• Ветер: ${weatherData.windSpeed} м/с")
            appendLine("• Время суток: ${DateUtils.getTimeOfDayInRussian(weatherData.timeOfDay)}")
            appendLine("• Сезон: ${DateUtils.getSeasonInRussian(weatherData.season)}")
            appendLine("• Фаза луны: ${DateUtils.getMoonPhaseInRussian(weatherData.moonPhase)}")
            appendLine("• Нерест: ${DateUtils.getSpawningInRussian(weatherData.isSpawning)}")
        }.toString()
    }

    // Открытие детального экрана для рыбы
    private fun openFishDetails(fish: Fish) {
        val intent = Intent(this, FishDetailActivity::class.java)
        intent.putExtra("fishName", fish.name)
        startActivity(intent)
    }
}
