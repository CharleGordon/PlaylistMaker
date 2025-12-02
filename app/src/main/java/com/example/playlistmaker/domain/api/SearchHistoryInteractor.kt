package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun readHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}