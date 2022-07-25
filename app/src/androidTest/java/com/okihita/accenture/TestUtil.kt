package com.okihita.accenture

import com.okihita.accenture.data.model.GitHubUser

fun generateUsers(size: Int): List<GitHubUser> {
    val users = mutableListOf<GitHubUser>()
    (1..size).forEach {
        users.add(
            GitHubUser(
                id = it,
                login = "hello$it",
                avatar_url = "avatar",
                gravatar_id = "gravatar"
            )
        )
    }
    return users.toList()
}