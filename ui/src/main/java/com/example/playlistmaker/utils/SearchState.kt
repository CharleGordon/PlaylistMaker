package com.example.playlistmaker.utils

import com.example.domain.models.Track

sealed class SearchState {
    object Loading : SearchState()
    object Default : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
    data class Error(val message: String) : SearchState()
}