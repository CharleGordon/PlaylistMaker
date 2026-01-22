package com.example.playlistmaker.app

import android.app.Application
import com.example.domain.api.ThemeInteractor
import com.example.playlistmaker.di.appModule
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.domainModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
    }

    private val themeInteractor: ThemeInteractor by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule))
        }

        themeInteractor.updateThemeSetting(themeInteractor.getThemeSettings())

    }

}