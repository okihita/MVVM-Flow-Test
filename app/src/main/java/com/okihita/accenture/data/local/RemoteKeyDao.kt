package com.okihita.accenture.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<GitHubRemoteKey>)

    @Query("SELECT * FROM remote_key")
    suspend fun getAllKeys(): List<GitHubRemoteKey>

}