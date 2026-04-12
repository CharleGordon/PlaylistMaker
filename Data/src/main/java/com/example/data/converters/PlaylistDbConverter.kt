package com.example.data.converters

import com.example.data.db.entity.PlaylistEntity
import com.example.domain.models.Playlist


class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity? {
        return playlist.trackIds?.let {
            PlaylistEntity(
                id = playlist.id,
                title = playlist.title,
                description = playlist.description,
                imagePath = playlist.imagePath,
                trackIds = it,
                tracksCount = playlist.tracksCount
            )
        }
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {

        return Playlist(
            id = playlistEntity.id,
            title = playlistEntity.title,
            description = playlistEntity.description,
            imagePath = playlistEntity.imagePath,
            trackIds = playlistEntity.trackIds,
            tracksCount = playlistEntity.tracksCount
        )
    }
}