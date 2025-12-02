package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(
    private val searchHistoryRepository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun readHistory(): List<Track> {
        return searchHistoryRepository.readHistory()
    }

    override fun addTrackToHistory(track: Track) {
        val currentHistory = readHistory().toMutableList()

        currentHistory.removeIf { it.trackId == track.trackId }
        currentHistory.add(0, track)
        searchHistoryRepository.saveHistory(currentHistory)
    }

    override fun clearHistory() {
        searchHistoryRepository.clearHistory()
    }

}