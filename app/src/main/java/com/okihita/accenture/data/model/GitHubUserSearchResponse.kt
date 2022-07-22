package com.okihita.accenture.data.model

import com.google.gson.annotations.SerializedName

data class GitHubUserSearchResponse(

    val message: String?,

    val incomplete_results: Boolean,
    @SerializedName("items") val users: List<GitHubUser>,
    val total_count: Int
)