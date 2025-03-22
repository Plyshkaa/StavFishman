package com.example.fishingforecastappstav

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.ContextCompat
import com.example.fishingforecastappstav.mainScreen.GuideActivity

class AboutScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        // Получаем нижнее меню и устанавливаем выбранный пункт
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.selectedItemId = R.id.nav_about
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_guide -> {
                    startActivity(Intent(this, GuideActivity::class.java))
                    true
                }
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

        // Настройка прозрачного статус-бара
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.statusBarColor = Color.TRANSPARENT
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
