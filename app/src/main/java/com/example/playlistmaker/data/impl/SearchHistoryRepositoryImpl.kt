package com.example.playlistmaker.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : SearchHistoryRepository {

    override fun readHistory(): List<Track> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)

        if (json == null) {
            return emptyList()
        }
        return gson.fromJson(json, Array<Track>::class.java).toList()
    }

    override fun saveHistory(tracks: List<Track>) {
        val tracksToSave = if (tracks.size > MAX_HISTORY_SIZE) tracks.subList(0, MAX_HISTORY_SIZE)
        else tracks
        val json = gson.toJson(tracksToSave)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    companion object {
        const val SEARCH_HISTORY_KEY = "search_history_key"
        const val MAX_HISTORY_SIZE = 10
    }
}