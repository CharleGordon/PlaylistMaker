package com.example.data.network

interface NetworkClient {
    fun doRequest(dto: Any): com.example.data.dto.Response
}