package com.example.playlistmaker.utils

import com.example.domain.models.Playlist

sealed interface PlaylistsState {
    object Empty : PlaylistsState
    data class Content(val playlists: List<Playlist>) : PlaylistsState
}