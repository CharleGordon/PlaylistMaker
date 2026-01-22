package com.example.domain.api

import com.example.domain.models.ThemeSettings

interface ThemeInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}