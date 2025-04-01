package com.example.fishingforecastappstav.mainScreen


import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.fishingforecastappstav.R

class MapActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        // Чтобы ссылки открывались внутри WebView, а не в браузере:
        webView.webViewClient = WebViewClient()

        // Загрузка карты по URL из iframe
        val mapUrl =
            "https://www.google.ru/maps/d/embed?mid=1WQ1s7X6JYl1-RUIHFGLuv0gsQyJmt8E&ehbc=2E312F"
        webView.loadUrl(mapUrl)
    }
}
