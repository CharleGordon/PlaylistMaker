package com.example.data.network

import com.example.data.dto.Response
import com.example.data.dto.SearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient : NetworkClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val searchApiService = retrofit.create(SearchApiService::class.java)

    override fun doRequest(dto: Any): Response {
        if (dto is SearchRequest) {
            val response = searchApiService.search(dto.text).execute()

            val body = response.body() ?: Response()

            return body.apply { isSuccessful = response.isSuccessful }
        } else {
            return Response().apply { isSuccessful = false }
        }
    }

    companion object {
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}