package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.ThemeInteractor

class App : Application() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        themeInteractor = Creator.provideThemeInteractor(this)
        themeInteractor.applyCurrentTheme()

    }

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
    }
}