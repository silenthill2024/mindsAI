package com.example.mindsai.api

import retrofit2.http.GET
import retrofit2.http.Query

interface BloggerService {

    @GET("blogs/7139221524294473112/posts")

    suspend fun getPosts(

        @Query("key")
        apiKey:String

    ): BloggerResponse

}