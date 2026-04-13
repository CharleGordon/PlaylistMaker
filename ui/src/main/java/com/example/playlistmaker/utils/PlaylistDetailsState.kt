package com.example.playlistmaker.utils

import com.example.domain.models.Playlist
import com.example.domain.models.Track

data class PlaylistDetailsState(
    val playlist: Playlist,
    val tracks: List<Track>
)