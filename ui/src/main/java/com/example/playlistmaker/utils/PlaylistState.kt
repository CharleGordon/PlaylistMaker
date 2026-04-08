package com.example.playlistmaker.utils

import com.example.domain.models.Playlist
import com.example.domain.models.Track

sealed class PlaylistsState {
    object Empty : PlaylistsState()
    data class Content(val playlists: List<Playlist>) : PlaylistsState()
}