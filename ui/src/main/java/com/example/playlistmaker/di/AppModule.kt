package com.example.playlistmaker.di

import com.example.playlistmaker.presentation.viewmodel.main.MainActivityViewModel
import com.example.playlistmaker.presentation.viewmodel.media.FavoriteTracksFragmentViewModel
import com.example.playlistmaker.presentation.viewmodel.media.MediaActivityViewModel
import com.example.playlistmaker.presentation.viewmodel.media.PlaylistFragmentViewModel
import com.example.playlistmaker.presentation.viewmodel.player.AudioPlayerActivityViewModel
import com.example.playlistmaker.presentation.viewmodel.search.SearchActivityViewModel
import com.example.playlistmaker.presentation.viewmodel.settings.SettingsActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<MainActivityViewModel> {
        MainActivityViewModel()
    }

    viewModel<AudioPlayerActivityViewModel> {
        AudioPlayerActivityViewModel()
    }

    viewModel<SearchActivityViewModel> {
        SearchActivityViewModel(
            trackInteractor = get(),
            searchHistoryInteractor = get()
        )
    }

    viewModel<SettingsActivityViewModel> {
        SettingsActivityViewModel(
            sharingInteractor = get(),
            themeInteractor = get()
        )
    }

    viewModel<MediaActivityViewModel> {
        MediaActivityViewModel()
    }

    viewModel<PlaylistFragmentViewModel> {
        PlaylistFragmentViewModel()
    }

    viewModel<FavoriteTracksFragmentViewModel> {
        FavoriteTracksFragmentViewModel()
    }
}