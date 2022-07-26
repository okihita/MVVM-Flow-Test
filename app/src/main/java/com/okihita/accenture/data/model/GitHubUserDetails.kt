package com.okihita.accenture.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_details")
data class GitHubUserDetails(
    val avatar_url: String = "",
    val bio: String? = null,
    val blog: String = "",
    val compString: String? = null,
    val created_at: String = "",
    val email: String? = null,
    val events_url: String = "",
    val followers: Int = 0,
    val followers_url: String = "",
    val following: Int = 0,
    val following_url: String = "",
    val gists_url: String = "",
    val gravatar_id: String = "",
    val hireable: String? = null,
    val html_url: String = "",

    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val location: String = "",
    val login: String = "",
    val name: String = "",
    val node_id: String = "",
    val organizations_url: String = "",
    val public_gists: Int = 0,
    val public_repos: Int = 0,
    val received_events_url: String = "",
    val repos_url: String = "",
    val site_admin: Boolean = false,
    val starred_url: String = "",
    val subscriptions_url: String = "",
    val twitter_username: String? = null,
    val type: String = "",
    val updated_at: String = "",
    val url: String = ""
)
// Generated with JsonToKotlinClass plugin in Android Studio