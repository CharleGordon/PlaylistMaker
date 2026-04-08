package com.example.data.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.data.converters.PlaylistDbConverter
import com.example.data.converters.TrackDbConverter
import com.example.data.db.dao.PlaylistDao
import com.example.data.db.dao.TrackInPlaylistDao
import com.example.domain.api.PlaylistRepository
import com.example.domain.models.Playlist
import com.example.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val context: Context,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConverter: TrackDbConverter,
    private val trackInPlaylistDao: TrackInPlaylistDao
) : PlaylistRepository {

    override suspend fun savePlaylist(playlist: Playlist) {
        val entity = playlistDbConverter.map(playlist)
        playlistDao.insertPlaylist(entity)
    }

    override suspend fun saveImageToPrivateStorage(uri: String): String {
        val uriString = Uri.parse(uri)
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "my_playlists")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uriString)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    BitmapFactory.decodeStream(inputStream)
                        .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
                }
            }
        }
        return file.absolutePath
    }

    override suspend fun addTrackToPlaylist(track: Track?, playlist: Playlist) {

        val updatedTrackIds = playlist.trackIds.toMutableList()
        track?.trackId?.toLong()?.let { updatedTrackIds.add(it) }
        val updatedPlaylist = playlist.copy(
            trackIds = updatedTrackIds,
            tracksCount = updatedTrackIds.size
        )

        playlistDao.updatePlaylist(playlistDbConverter.map(updatedPlaylist))
        track?.let { trackDbConverter.mapToEntity(it) }?.let { trackInPlaylistDao.insertTrack(it) }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                playlistDbConverter.map(entity)
            }
        }
    }
}