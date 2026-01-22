package com.example.domain.impl

import com.example.domain.api.TrackInteractor
import com.example.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: com.example.domain.api.TracksRepository) :
    com.example.domain.api.TrackInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(text: String, consumer: com.example.domain.api.TrackInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.searchTracks(text))
        }
    }
}