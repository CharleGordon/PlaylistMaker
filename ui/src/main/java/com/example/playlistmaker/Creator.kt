package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import com.example.data.impl.ExternalNavigator
import com.example.data.impl.SearchHistoryRepositoryImpl
import com.example.data.impl.ThemeRepositoryImpl
import com.example.data.impl.TracksRepositoryImpl
import com.example.data.network.RetrofitClient
import com.example.domain.api.SearchHistoryInteractor
import com.example.domain.api.SearchHistoryRepository
import com.example.domain.api.SharingInteractor
import com.example.domain.api.SharingRepository
import com.example.domain.api.ThemeInteractor
import com.example.domain.api.ThemeRepository
import com.example.domain.api.TrackInteractor
import com.example.domain.api.TracksRepository
import com.example.domain.impl.SearchHistoryInteractorImpl
import com.example.domain.impl.SharingInteractorImpl
import com.example.domain.impl.ThemeInteractorImpl
import com.example.domain.impl.TracksInteractorImpl
import com.google.gson.Gson

object Creator {

    private fun getSharedPreferences(context: Context) =
        context.getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient())
    }

    private fun getSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            getSharedPreferences(context),
            Gson()
        )
    }

    private fun getThemeRepository(context: Context) : ThemeRepository {
        return ThemeRepositoryImpl(getSharedPreferences(context))
    }

    private fun getSharingRepository(context: Context): SharingRepository {
        return ExternalNavigator(context)
    }

    fun provideTracksInteractor(): TrackInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(context: Context) : SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(
            getSearchHistoryRepository(
                context
            )
        )
    }

    fun provideThemeInteractor(context: Context) : ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getSharingRepository(context))
    }

}