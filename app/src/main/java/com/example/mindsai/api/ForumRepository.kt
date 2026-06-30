package com.example.mindsai.repository

import com.example.mindsai.api.RetrofitInstance

class ForumRepository {

    suspend fun getPosts() =

        RetrofitInstance.api.getPosts(

            "AIzaSyAlJ1Mi-p9kPz0lqltAPwQhKD1JtJpDwCM"
        )

}