package com.example.data.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.domain.api.ThemeRepository
import com.example.domain.models.ThemeSettings

class ThemeRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeRepository {

    companion object {
        const val DARK_THEME_KEY = "dark_theme_key"
    }

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        return ThemeSettings(isDarkTheme = isDark)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, settings.isDarkTheme)
            .apply()

        applyTheme(settings.isDarkTheme)
    }

    private fun applyTheme(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}