package com.example.domain.models

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val playbackTime: String = "00:00",
    val isPlayerReady: Boolean = false,
    val track: Track? = null
)
