package com.example.domain.api

import com.example.domain.models.Playlist
import com.example.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun savePlaylist(playlist: Playlist)
    suspend fun saveImageToPrivateStorage(uri: String): String
    suspend fun addTrackToPlaylist(track: Track?, playlist: Playlist)
    suspend fun getPlaylistById(id: Int): Playlist
    suspend fun getTracksByIds(ids: List<Int>): List<Track>
    suspend fun deleteTrackFromPlaylist(trackId: Int, playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun removeTrackFromPlaylist(trackId: Int, playlistId: Int)
    fun getAllPlaylists(): Flow<List<Playlist>>
}