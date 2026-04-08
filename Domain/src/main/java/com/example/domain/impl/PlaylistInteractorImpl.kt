package com.example.domain.impl

import com.example.domain.api.PlaylistInteractor
import com.example.domain.api.PlaylistRepository
import com.example.domain.models.Playlist
import com.example.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun savePlaylist(playlist: Playlist) {
        repository.savePlaylist(playlist)
    }

    override suspend fun saveImageToPrivateStorage(uri: String): String {
        return repository.saveImageToPrivateStorage(uri)
    }

    override suspend fun addTrackToPlaylist(track: Track?, playlist: Playlist) {
        repository.addTrackToPlaylist(track, playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }
}