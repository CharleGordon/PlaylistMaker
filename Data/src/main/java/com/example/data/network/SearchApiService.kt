package com.example.data.network

import com.example.data.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {

    @GET("/search?entity=song")
    fun search(@Query("term") text: String) : Call<SearchResponse>
}