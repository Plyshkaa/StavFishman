package com.example.fishingforecastappstav

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fishingforecastappstav.databinding.ActivityGuideBinding
import com.example.fishingforecastappstav.mainScreen.GuideAdapter
import com.example.fishingforecastappstav.mainScreen.GuideCategory
import com.google.android.material.bottomnavigation.BottomNavigationView

class GuideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        // Настройка прозрачного статус-бара
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
    }



    private fun setupRecyclerView() {
        val categories = listOf(
            GuideCategory("Техники ловли", R.drawable.fishing_techniques),
            GuideCategory("Снаряжение и оборудование", R.drawable.equipment),
            GuideCategory("Сезонные советы", R.drawable.seasonal_advice),
            GuideCategory("Советы от экспертов", R.drawable.expert_advice),
            GuideCategory("Экология", R.drawable.ecology),
            GuideCategory("Часто задаваемые вопросы", R.drawable.faq),
            GuideCategory("Приманки и прикормки", R.drawable.baits_feeding)
        )

        val adapter = GuideAdapter(categories) { category ->
            Toast.makeText(this, "Выбрано: ${category.title}", Toast.LENGTH_SHORT).show()
            // Здесь делай переход на экран деталей категории
        }

        binding.rvGuideCategories.layoutManager = LinearLayoutManager(this)
        binding.rvGuideCategories.adapter = adapter
    }
}
