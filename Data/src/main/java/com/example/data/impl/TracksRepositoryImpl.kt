package com.example.data.impl

import com.example.data.network.NetworkClient
import com.example.domain.api.TracksRepository
import com.example.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(text: String): List<Track>? {
        try {
            val response = networkClient.doRequest(com.example.data.dto.SearchRequest(text))
            if (response.isSuccessful) {
                return (response as com.example.data.dto.SearchResponse).results.map {
                    Track(
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.trackId.toString(),
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    )
                }
            } else {
                return emptyList()
            }
        } catch (e: Exception) {
            return null
        }
    }
}