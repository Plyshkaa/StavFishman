package com.example.fishingforecastappstav

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fishingforecastappstav.mainScreen.Fish

class FishDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fish_detail)

        // Получаем рыбу из интента
        val fish = intent.getSerializableExtra("fish") as? Fish
        fish?.let {
            // Находим нужные View и заполняем данными
            val tvName = findViewById<TextView>(R.id.tvFishNameDetail)
            val ivIcon = findViewById<ImageView>(R.id.ivFishIconDetail)
            val tvDescription = findViewById<TextView>(R.id.tvFishDescriptionDetail)

            tvName.text = it.name
            ivIcon.setImageResource(it.iconResId)
            tvDescription.text = it.description
        }
    }
}
