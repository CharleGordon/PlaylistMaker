package com.example.domain.api

import com.example.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {
    fun searchTracks(text: String): Flow<Pair<List<Track>?, String?>>
}