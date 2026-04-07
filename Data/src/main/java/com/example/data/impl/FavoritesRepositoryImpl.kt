package com.example.data.impl

import com.example.data.converters.TrackDbConvertor
import com.example.data.db.dao.FavoriteTracksDao
import com.example.domain.api.FavoritesRepository
import com.example.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val favoriteTracksDao: FavoriteTracksDao,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksDao.getFavoriteTracks().map { tracks ->
            tracks.map { trackEntity -> trackDbConvertor.map(trackEntity) }
        }
    }

    override suspend fun addTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        favoriteTracksDao.insertTrack(trackEntity)
    }

    override suspend fun removeTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        favoriteTracksDao.deleteTrack(trackEntity)
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return favoriteTracksDao.isFavorite(trackId)
    }
}