package com.example.fishingforecastappstav.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

object ResourceUtils {
    
    fun getColor(context: Context, colorResId: Int): Int {
        return ContextCompat.getColor(context, colorResId)
    }
    
    fun getDrawable(context: Context, drawableResId: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawableResId)
    }
    
    fun getString(context: Context, stringResId: Int): String {
        return context.getString(stringResId)
    }
    
    fun getString(context: Context, stringResId: Int, vararg formatArgs: Any): String {
        return context.getString(stringResId, *formatArgs)
    }
    
    fun dpToPx(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
    
    fun pxToDp(context: Context, px: Float): Int {
        val density = context.resources.displayMetrics.density
        return (px / density).toInt()
    }
    
    fun isColorDark(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }
    
    fun getContrastColor(backgroundColor: Int): Int {
        return if (isColorDark(backgroundColor)) Color.WHITE else Color.BLACK
    }
}
