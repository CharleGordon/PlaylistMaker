package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun isDarkTheme(): Boolean
    fun saveTheme(isDark: Boolean)
}