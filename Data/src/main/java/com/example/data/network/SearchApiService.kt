package com.example.data.network

import com.example.data.dto.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {

    @GET("/search?entity=song")
    suspend fun search(@Query("term") text: String) : SearchResponse
}