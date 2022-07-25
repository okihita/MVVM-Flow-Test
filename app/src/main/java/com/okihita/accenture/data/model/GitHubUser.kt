package com.okihita.accenture.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class GitHubUser(

    @PrimaryKey(autoGenerate = true)
    val rowId: Int = 0,

    val id: Int,
    val login: String,
    val avatar_url: String,
    val gravatar_id: String
)