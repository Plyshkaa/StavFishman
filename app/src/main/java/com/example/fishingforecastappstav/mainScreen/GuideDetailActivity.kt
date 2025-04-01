package com.example.fishingforecastappstav.mainScreen

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import com.example.fishingforecastappstav.R
import com.example.fishingforecastappstav.databinding.ActivityGuideDetailBinding
import com.google.gson.Gson
import androidx.core.view.isGone



class GuideDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGuideDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuideDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        window.statusBarColor = Color.TRANSPARENT

        val guideName = intent.getStringExtra("guideName") ?: return
        val fileName = getFileNameForGuide(guideName)

        val json = assets.open(fileName).bufferedReader().use { it.readText() }
        val guideDetail = Gson().fromJson(json, GuideDetail::class.java)

        binding.titleBlock1.text = guideDetail.title
        guideDetail.sections.getOrNull(0)?.let {
            setupExpandableBlock(binding.titleBlock1, binding.contentBlock1, binding.arrowBlock1, it)
        }
    }

    private fun setupExpandableBlock(
        titleView: TextView,
        contentView: LinearLayout,
        arrowView: ImageView,
        block: GuideBlock
    ) {
        titleView.text = block.title
        contentView.removeAllViews()
        block.content.forEach { item ->
            when (item.type) {
                "text" -> {
                    val tv = TextView(this).apply { text = item.value; textSize = 14f }
                    contentView.addView(tv)
                }
                "image" -> {
                    val iv = ImageView(this).apply {
                        val resId = resources.getIdentifier(item.value, "drawable", packageName)
                        setImageResource(resId)
                        adjustViewBounds = true
                    }
                    contentView.addView(iv)
                }
                "link" -> {
                    val tvLink = TextView(this).apply {
                        text = item.label ?: item.value
                        setTextColor(Color.BLUE)
                        paintFlags = paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                        textSize = 14f
                        setOnClickListener {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.value)))
                        }
                    }
                    contentView.addView(tvLink)
                }
            }
        }
        titleView.setOnClickListener {
            if (contentView.isGone) {
                contentView.visibility = View.VISIBLE
                arrowView.rotation = 180f
            } else {
                contentView.visibility = View.GONE
                arrowView.rotation = 0f
            }
        }
    }

    private fun getFileNameForGuide(guideName: String): String = when (guideName) {
        "Техники ловли" -> "techniques.json"
        "Снаряжение и оборудование" -> "guide_gear.json"
        else -> "guide_default.json"
    }
}



