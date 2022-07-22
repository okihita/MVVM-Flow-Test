package com.okihita.accenture.data.remote

import com.okihita.accenture.data.model.GitHubUserSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApi {

    @GET("search/users")
    suspend fun getUsers(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 10,
        @Query("page") page: Int = 1
    ): GitHubUserSearchResponse

}