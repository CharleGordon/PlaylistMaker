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
        if (entity != null) {
            playlistDao.insertPlaylist(entity)
        }
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

    override suspend fun addTrackToPlaylist(track: Track?, playlist: Playlist) = withContext(Dispatchers.IO) {
        if (track == null) return@withContext

        val cleanedString = playlist.trackIds?.filter { it.isDigit() || it == ',' } ?: ""

        val currentIds = cleanedString.split(",")
            .filter { it.isNotEmpty() }
            .mapNotNull { it.toIntOrNull() }
            .toMutableList()

        val trackId = track.trackId

        if (!currentIds.contains(trackId)) {
            currentIds.add(trackId)

            val updatedPlaylist = playlist.copy(
                trackIds = currentIds.joinToString(","),
                tracksCount = currentIds.size
            )

            playlistDbConverter.map(updatedPlaylist)?.let { playlistDao.updatePlaylist(it) }
            trackInPlaylistDao.insertTrack(trackDbConverter.mapToEntity(track))
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                playlistDbConverter.map(entity)
            }
        }
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        val entity = playlistDao.getPlaylistById(id)
        return playlistDbConverter.map(entity)
    }

    override suspend fun getTracksByIds(ids: List<Int>): List<Track> = withContext(Dispatchers.IO) {
        val entities = trackInPlaylistDao.getTracksByIds(ids)
        entities.map { trackDbConverter.map(it) }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDbConverter.map(playlist)?.let { playlistDao.updatePlaylist(it) }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDbConverter.map(playlist)?.let { playlistDao.deletePlaylist(it) }
    }
}