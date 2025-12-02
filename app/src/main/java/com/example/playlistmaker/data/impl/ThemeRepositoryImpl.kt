package com.example.playlistmaker.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    override fun isDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun saveTheme(isDark: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, isDark)
            .apply()
    }

    companion object {
        const val DARK_THEME_KEY = "dark_theme_key"
    }
}