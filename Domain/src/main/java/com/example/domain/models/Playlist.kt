package com.example.domain.models

data class Playlist(
    val id: Int = 0,
    val title: String,
    val description: String?,
    val imagePath: String?,
    val trackIds: String?,
    val tracksCount: Int = 0
)