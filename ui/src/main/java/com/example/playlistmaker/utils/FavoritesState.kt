package com.example.playlistmaker.utils

import com.example.domain.models.Track

sealed interface FavoritesState {
    object Empty : FavoritesState
    data class Content(val tracks: List<Track>) : FavoritesState
}