package com.example.fishingforecastappstav.data.local

import android.content.Context

class FavoritesStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getFavorites(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
    }

    fun isFavorite(name: String): Boolean {
        return getFavorites().contains(name)
    }

    fun toggleFavorite(name: String) {
        val current = getFavorites().toMutableSet()
        if (current.contains(name)) {
            current.remove(name)
        } else {
            current.add(name)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, current).apply()
    }

    companion object {
        private const val PREFS_NAME = "favorites_store"
        private const val KEY_FAVORITES = "favorites"
    }
}
