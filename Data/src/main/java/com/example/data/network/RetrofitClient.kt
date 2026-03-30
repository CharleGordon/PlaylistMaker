package com.example.data.network

import android.content.Context
import com.example.data.dto.Response
import com.example.data.dto.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitClient(private val searchApiService: SearchApiService, context: Context) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {

        if (dto !is SearchRequest) {
            return Response().apply { resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = searchApiService.search(dto.text)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }

}