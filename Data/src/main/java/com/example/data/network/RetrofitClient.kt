package com.example.data.network

import android.content.Context
import com.example.data.dto.Response
import com.example.data.dto.SearchRequest

class RetrofitClient(private val searchApiService: SearchApiService, context: Context) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is SearchRequest) {
            val response = searchApiService.search(dto.text).execute()

            val body = response.body() ?: Response()

            return body.apply { isSuccessful = response.isSuccessful }
        } else {
            return Response().apply { isSuccessful = false }
        }
    }
}