package com.example.fishingforecastappstav

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.google.gson.Gson
import com.example.fishingforecastappstav.databinding.ActivityFishDetailBinding
import com.example.fishingforecastappstav.mainScreen.FishDetail

class FishDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFishDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFishDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.headerGeneral.setOnClickListener {
            toggleVisibility(binding.contentGeneral, binding.arrowGeneral)
        }


        // Получаем имя рыбы из Intent
        val fishName = intent.getStringExtra("fishName") ?: return

        // Настраиваем тулбар
        binding.tvFishNameToolbar.text = fishName
        binding.btnBack.setOnClickListener {
            finish()
        }

        val fileName = getFileNameForFish(fishName)

        // Загружаем JSON из assets
        val json = assets.open(fileName).bufferedReader().use { it.readText() }

        // Парсим JSON в объект FishDetail с помощью Gson
        val fishDetail = Gson().fromJson(json, FishDetail::class.java)

        // Заполняем UI
        binding.tvFishNameDetail.text = fishDetail.name
        binding.tvFishShortDescDetail.text = fishDetail.shortDescription
        val iconResId = resources.getIdentifier(fishDetail.iconResName, "drawable", packageName)
        binding.ivFishIconDetail.setImageResource(iconResId)
        binding.contentGeneral.text = fishDetail.generalInfo
        binding.contentBaits.text = fishDetail.baitsInfo
        binding.contentSeasons.text = fishDetail.seasonsInfo
        binding.contentFeeding.text = fishDetail.feedingInfo

        // При желании можно добавить обработчики нажатия для раскрытия/скрытия блоков
        // Например:
        binding.headerGeneral.setOnClickListener {
            toggleVisibility(binding.contentGeneral, binding.arrowGeneral)
        }
        binding.headerBaits.setOnClickListener {
            toggleVisibility(binding.contentBaits, binding.arrowBaits)
        }
        binding.headerSeasons.setOnClickListener {
            toggleVisibility(binding.contentSeasons, binding.arrowSeasons)
        }
        binding.headerFeeding.setOnClickListener {
            toggleVisibility(binding.contentFeeding, binding.arrowFeeding)
        }
    }

    private fun toggleVisibility(contentView: View, arrowView: ImageView) {
        if (contentView.visibility == View.GONE) {
            contentView.visibility = View.VISIBLE
            arrowView.setImageResource(R.drawable.ic_expand_less2) // стрелка вверх
        } else {
            contentView.visibility = View.GONE
            arrowView.setImageResource(R.drawable.ic_expand_more3) // стрелка вниз
        }
    }

    private fun getFileNameForFish(fishName: String): String {
        return when (fishName) {
            "Окунь" -> "fish_okun.json"
            "Щука" -> "fish_shuka.json"
            "Лещ" -> "fish_lesh.json"
            "Карась" -> "fish_karas.json"
            "Плотва" -> "fish_plotva.json"
            "Судак" -> "fish_sudak.json"
            "Сазан" -> "fish_sazan.json"
            else -> "fish_default.json"
        }
    }
}
