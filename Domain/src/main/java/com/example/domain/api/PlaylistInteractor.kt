package com.example.domain.api

import com.example.domain.models.Playlist
import com.example.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    suspend fun saveImageToPrivateStorage(uri: String): String
    suspend fun addTrackToPlaylist(track: Track?, playlist: Playlist)
    fun getAllPlaylists(): Flow<List<Playlist>>
}