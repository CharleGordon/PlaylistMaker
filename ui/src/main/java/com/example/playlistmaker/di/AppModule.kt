package com.example.playlistmaker.di

import androidx.lifecycle.SavedStateHandle
import com.example.playlistmaker.presentation.viewmodel.media.FavoriteTracksFragmentViewModel
import com.example.playlistmaker.presentation.viewmodel.media.MediaFragmentViewModel
import com.example.playlistmaker.presentation.viewmodel.media.PlaylistFragmentViewModel
import com.example.playlistmaker.presentation.viewmodel.player.AudioPlayerFragmentViewModel
import com.example.playlistmaker.presentation.viewmodel.search.SearchFragmentViewModel
import com.example.playlistmaker.presentation.viewmodel.settings.SettingsFragmentViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<AudioPlayerFragmentViewModel> {
        AudioPlayerFragmentViewModel()
    }

    viewModel<SearchFragmentViewModel> { (handle: SavedStateHandle) ->
        SearchFragmentViewModel(
            savedStateHandle = handle,
            trackInteractor = get(),
            searchHistoryInteractor = get()
        )
    }

    viewModel<SettingsFragmentViewModel> {
        SettingsFragmentViewModel(
            sharingInteractor = get(),
            themeInteractor = get()
        )
    }

    viewModel<MediaFragmentViewModel> {
        MediaFragmentViewModel()
    }

    viewModel<PlaylistFragmentViewModel> {
        PlaylistFragmentViewModel()
    }

    viewModel<FavoriteTracksFragmentViewModel> {
        FavoriteTracksFragmentViewModel()
    }
}