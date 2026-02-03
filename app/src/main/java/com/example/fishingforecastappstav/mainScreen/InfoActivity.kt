package com.example.fishingforecastappstav.mainScreen

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.fishingforecastappstav.MainActivity
import com.example.fishingforecastappstav.R
import com.example.fishingforecastappstav.utils.Constants
import com.google.android.material.bottomnavigation.BottomNavigationView

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        supportActionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, true)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.white)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.white)

        setupBottomNav()
        populateSections()
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNav.selectedItemId = R.id.nav_info
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_info -> true
                R.id.nav_map -> {
                    openMap()
                    true
                }
                R.id.nav_about -> {
                    startActivity(Intent(this, AboutScreen::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun populateSections() {
        val container = findViewById<LinearLayout>(R.id.infoContainer)
        val inflater = LayoutInflater.from(this)

        val sections = listOf(
            Section(
                title = "Как привязать крючок",
                items = listOf(
                    SectionItem("Классическая петля — быстрый и надежный вариант."),
                    SectionItem("Клинч-узел подходит для монофильной лески."),
                    SectionItem("Паломар — один из самых прочных узлов для плетёнки."),
                    SectionItem("Совет: оставляй 3–4 мм хвостика после затяжки.")
                )
            ),
            Section(
                title = "Как выбрать леску",
                items = listOf(
                    SectionItem("Для начинающих: монофильная 0.18–0.25 мм."),
                    SectionItem("Для хищника: плетёнка + флюорокарбоновый поводок."),
                    SectionItem("Проверяй разрывную нагрузку и тест на узле."),
                    SectionItem("Чем прозрачнее вода, тем тоньше леска.")
                )
            ),
            Section(
                title = "Как выбрать удилище",
                items = listOf(
                    SectionItem("Поплавочная ловля: 4–6 м, средний строй."),
                    SectionItem("Спиннинг: тест 5–20 г для универсальной рыбалки."),
                    SectionItem("Длина удилища зависит от типа водоёма."),
                    SectionItem("Для берега удобнее длиннее, для лодки — короче.")
                )
            ),
            Section(
                title = "Безопасность и погода",
                items = listOf(
                    SectionItem("Не выходи на лёд при температуре выше 0°C несколько дней подряд."),
                    SectionItem("Сильный ветер снижает клёв и делает рыбалку опаснее."),
                    SectionItem("Проверяй прогноз перед выездом.")
                )
            ),
            Section(
                title = "Сезонность и поведение рыб",
                items = listOf(
                    SectionItem("Зимой активнее щука, судак и окунь."),
                    SectionItem("Весной и осенью многие виды более активны."),
                    SectionItem("Теплолюбивые рыбы (сазан, карась) зимой обычно пассивны.")
                )
            )
        )

        sections.forEach { section ->
            val view = inflater.inflate(R.layout.expandable_section, container, false)
            val titleView = view.findViewById<TextView>(R.id.sectionTitle)
            val contentView = view.findViewById<LinearLayout>(R.id.sectionContent)
            val arrowView = view.findViewById<ImageView>(R.id.arrowIcon)

            titleView.text = section.title
            contentView.removeAllViews()
            section.items.forEach { item ->
                val tv = TextView(this).apply {
                    text = "• ${item.value}"
                    textSize = 14f
                }
                contentView.addView(tv)
            }

            val toggle = {
                if (contentView.visibility == View.GONE) {
                    contentView.visibility = View.VISIBLE
                    arrowView.rotation = 180f
                } else {
                    contentView.visibility = View.GONE
                    arrowView.rotation = 0f
                }
            }

            titleView.setOnClickListener { toggle() }
            arrowView.setOnClickListener { toggle() }

            container.addView(view)
        }
    }

    private fun openMap() {
        val uri = Uri.parse(Constants.MAP_URL)
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, uri)
    }

    private data class Section(val title: String, val items: List<SectionItem>)

    private data class SectionItem(val value: String)
}
