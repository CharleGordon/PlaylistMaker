package com.example.playlistmaker.di

import com.example.domain.api.SearchHistoryInteractor
import com.example.domain.api.SharingInteractor
import com.example.domain.api.ThemeInteractor
import com.example.domain.api.TrackInteractor
import com.example.domain.impl.SearchHistoryInteractorImpl
import com.example.domain.impl.SharingInteractorImpl
import com.example.domain.impl.ThemeInteractorImpl
import com.example.domain.impl.TracksInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    factory<TrackInteractor> {
        TracksInteractorImpl(repository = get())
    }

    factory<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(searchHistoryRepository = get())
    }

    factory<ThemeInteractor> {
        ThemeInteractorImpl(themeRepository = get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(sharingRepository = get())
    }

}