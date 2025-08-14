package com.example.fishingforecastappstav.mainScreen

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.example.fishingforecastappstav.R

class AboutScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Получаем нижнее меню и устанавливаем выбранный пункт
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.selectedItemId = R.id.nav_about
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_map -> {
                    startActivity(Intent(this, MapActivity::class.java))
                    true
                }
                R.id.nav_about -> {
                    // Если уже на экране "О приложении", можно либо ничего не делать,
                    // либо перезагрузить экран, если это требуется.
                    true
                }
                else -> false
            }
        }

        // Настройка Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar_about)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            title = "О приложении"
        }
        toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.black))
        toolbar.setNavigationOnClickListener { onBackPressed() }

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
    }

    // Пример метода для открытия ВКонтакте
    fun openVK(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/stavribalka"))
        startActivity(intent)
    }

    // Пример метода для открытия Telegram
    fun openTelegram(view: View) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/Fishing_Stavropol"))
        startActivity(intent)
    }
}
