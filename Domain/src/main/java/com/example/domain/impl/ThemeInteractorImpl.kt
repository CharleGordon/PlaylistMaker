package com.example.domain.impl

import com.example.domain.api.ThemeInteractor
import com.example.domain.api.ThemeRepository
import com.example.domain.models.ThemeSettings

class ThemeInteractorImpl(
    private val themeRepository: ThemeRepository
) : ThemeInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return themeRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        themeRepository.updateThemeSetting(settings)
    }
}