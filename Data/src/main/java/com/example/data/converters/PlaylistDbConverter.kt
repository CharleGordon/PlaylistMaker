package com.example.data.converters

import com.example.data.db.entity.PlaylistEntity
import com.example.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class PlaylistDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            title = playlist.title,
            description = playlist.description,
            imagePath = playlist.imagePath,
            trackIds = gson.toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Long>>() {}.type
        val trackIds: List<Long> = gson.fromJson(playlistEntity.trackIds, type) ?: emptyList()

        return Playlist(
            id = playlistEntity.id,
            title = playlistEntity.title,
            description = playlistEntity.description,
            imagePath = playlistEntity.imagePath,
            trackIds = trackIds,
            tracksCount = playlistEntity.tracksCount
        )
    }
}