package com.okihita.accenture.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.okihita.accenture.data.model.GitHubUser

@Database(
    entities = [GitHubUser::class, GitHubRemoteKey::class],
    version = 2,
    exportSchema = false
)
abstract class GitHubUserDatabase : RoomDatabase() {

    abstract val userDao: GitHubUserDao
    abstract val remoteKeyDao: RemoteKeyDao
}