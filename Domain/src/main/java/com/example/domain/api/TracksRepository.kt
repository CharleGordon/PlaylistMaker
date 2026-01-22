package com.example.domain.api

import com.example.domain.models.Track

interface TracksRepository {
    fun searchTracks(text: String): List<Track>?
}