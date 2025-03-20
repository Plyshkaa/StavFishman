package com.example.fishingforecastappstav

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Обязательно вызываем super.onCreate
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_splash)

        // Задержка перед запуском MainActivity (например, 3000 мс)
        Handler(Looper.getMainLooper()).postDelayed({
            // Запускаем MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000)
    }
}