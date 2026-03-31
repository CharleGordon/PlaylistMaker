package com.example.domain.api

import com.example.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    suspend fun isFavorite(trackId: Int): Boolean
}