package com.example.domain.api

import com.example.domain.models.ThemeSettings

interface ThemeRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}