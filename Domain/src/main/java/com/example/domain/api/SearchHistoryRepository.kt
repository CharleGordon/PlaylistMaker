package com.example.domain.api

import com.example.domain.models.Track

interface SearchHistoryRepository {
    fun readHistory(): List<Track>
    fun saveHistory(tracks: List<Track>)
    fun clearHistory()
}