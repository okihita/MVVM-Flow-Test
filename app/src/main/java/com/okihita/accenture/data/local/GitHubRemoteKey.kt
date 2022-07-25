package com.okihita.accenture.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_key")
data class GitHubRemoteKey(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,

    val username: String,
    val prevKey: Int? = null,
    val nextKey: Int? = null
)