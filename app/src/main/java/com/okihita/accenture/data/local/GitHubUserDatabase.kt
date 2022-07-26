package com.okihita.accenture.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.okihita.accenture.data.model.GitHubUser
import com.okihita.accenture.data.model.GitHubUserDetails

@Database(
    entities = [
        GitHubUser::class,
        GitHubRemoteKey::class,
        GitHubUserDetails::class
    ],
    version = 3,
    exportSchema = false
)
abstract class GitHubUserDatabase : RoomDatabase() {

    abstract val userDao: GitHubUserDao
    abstract val remoteKeyDao: RemoteKeyDao
    abstract val userDetailsDao: GitHubUserDetailsDao
}