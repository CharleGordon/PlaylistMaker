package com.example.domain.impl

import com.example.domain.api.FavoritesInteractor
import com.example.domain.api.FavoritesRepository
import com.example.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoriteTracks()
    }

    override suspend fun addTrack(track: Track) {
        favoritesRepository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        favoritesRepository.removeTrack(track)
    }

    override suspend fun isFavorite(trackId: Int): Boolean {
        return favoritesRepository.isFavorite(trackId)
    }
}