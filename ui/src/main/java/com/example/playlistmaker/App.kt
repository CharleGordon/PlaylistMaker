package com.example.playlistmaker

import android.app.Application
import com.example.domain.api.ThemeInteractor

class App : Application() {

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
    }

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        themeInteractor = Creator.provideThemeInteractor(this)
        themeInteractor.updateThemeSetting(themeInteractor.getThemeSettings())

    }

}