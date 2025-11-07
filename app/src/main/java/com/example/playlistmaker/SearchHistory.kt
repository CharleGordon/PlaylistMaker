package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private val sharedPref : SharedPreferences) {

    fun read(): List<Track> {

        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null)

        if (json == null) {
            return emptyList()
        }
        return Gson().fromJson(json, Array<Track>::class.java).toList()
    }

    fun add(track: Track) {
        val history = read().toMutableList()

        history.removeIf { it.trackId == track.trackId}
        history.add(0, track)

        if (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.size - 1)
        }

        write(history)
    }

    fun clear() {
        sharedPref.edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    private fun write(tracks: List<Track>) {

        val json = Gson().toJson(tracks)

        sharedPref.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    companion object {
        const val SEARCH_HISTORY_KEY = "search_history_key"
        const val MAX_HISTORY_SIZE = 10
    }
}