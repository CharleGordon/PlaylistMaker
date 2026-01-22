package com.example.domain.api

import com.example.domain.models.Track


interface SearchHistoryInteractor {
    fun readHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}