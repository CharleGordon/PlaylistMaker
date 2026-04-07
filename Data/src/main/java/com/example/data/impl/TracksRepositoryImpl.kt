package com.example.data.impl

import com.example.data.dto.SearchRequest
import com.example.data.dto.SearchResponse
import com.example.data.network.NetworkClient
import com.example.domain.api.TracksRepository
import com.example.domain.models.Track
import com.example.domain.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(text: String): Flow<Resource<List<Track>>> = flow {
        try {
            val response = networkClient.doRequest(SearchRequest(text))

            when (response.resultCode) {
                -1 -> {
                    emit(Resource.Error("Проверьте подключение к интернету"))
                }

                200 -> {
                    val data = (response as SearchResponse).results.map {
                        Track(
                            it.trackName,
                            it.artistName,
                            it.trackTimeMillis,
                            it.artworkUrl100,
                            it.trackId,
                            it.collectionName,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.previewUrl
                        )
                    }
                    emit(Resource.Success(data))
                }

                else -> {
                    emit(Resource.Error("Ошибка сервера"))
                }
            }
        } catch (e: Exception) {
                emit(Resource.Error("Произошла непредвиденная ошибка"))
        }
    }
}