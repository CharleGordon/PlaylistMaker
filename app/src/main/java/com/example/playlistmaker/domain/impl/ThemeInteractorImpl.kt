package com.example.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeInteractorImpl(
    private val themeRepository: ThemeRepository
) : ThemeInteractor {

    override fun isDarkTheme(): Boolean {
        return themeRepository.isDarkTheme()
    }

    override fun switchTheme(isDark: Boolean) {
        themeRepository.saveTheme(isDark)
        applyTheme(isDark)
    }

    override fun applyCurrentTheme() {
        applyTheme(isDarkTheme())
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