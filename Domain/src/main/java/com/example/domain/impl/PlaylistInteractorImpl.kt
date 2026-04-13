package com.example.domain.impl

import com.example.domain.api.PlaylistInteractor
import com.example.domain.api.PlaylistRepository
import com.example.domain.models.Playlist
import com.example.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlin.collections.joinToString
import kotlin.collections.remove
import kotlin.text.split
import kotlin.text.toMutableList

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

    override suspend fun getPlaylistById(id: Int): Playlist {
        return repository.getPlaylistById(id)
    }

    override suspend fun getTracksByIds(ids: List<Int>): List<Track> {
        return repository.getTracksByIds(ids)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist) {
        val currentIds = playlist.trackIds?.split(",")?.toMutableList()
        currentIds?.remove(trackId.toString())
        val newTrackIds = currentIds?.joinToString(",")

        val updatedPlaylist = currentIds?.let {
            playlist.copy(
                trackIds = newTrackIds,
                tracksCount = it.size
            )
        }

        if (updatedPlaylist != null) {
            repository.updatePlaylist(updatedPlaylist)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }

    override suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Int) {
        repository.removeTrackFromPlaylist(trackId, playlistId)
    }
}