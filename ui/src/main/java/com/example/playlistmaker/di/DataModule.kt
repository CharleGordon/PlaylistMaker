package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.data.impl.ExternalNavigator
import com.example.data.impl.SearchHistoryRepositoryImpl
import com.example.data.impl.ThemeRepositoryImpl
import com.example.data.impl.TracksRepositoryImpl
import com.example.data.network.NetworkClient
import com.example.data.network.RetrofitClient
import com.example.data.network.SearchApiService
import com.example.domain.api.SearchHistoryRepository
import com.example.domain.api.SharingRepository
import com.example.domain.api.ThemeRepository
import com.example.domain.api.TracksRepository
import com.example.playlistmaker.app.App
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ITUNES_BASE_URL = "https://itunes.apple.com"

val dataModule = module {

    single<SearchApiService> {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SearchApiService::class.java)
    }

    single<SharedPreferences> {
        androidContext().getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    single<NetworkClient> {
        RetrofitClient(searchApiService = get(), androidContext())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get ())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(sharedPreferences = get(), gson = get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(sharedPreferences = get())
    }

    single<SharingRepository> {
        ExternalNavigator(context = androidContext())
    }

}