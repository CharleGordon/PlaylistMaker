package com.example.domain.api

import com.example.domain.models.Track
import com.example.domain.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(text: String): Flow<Resource<List<Track>>>
}