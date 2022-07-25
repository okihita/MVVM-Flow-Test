package com.okihita.accenture.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.okihita.accenture.data.model.GitHubUser

@Dao
interface GitHubUserDao {

    // Due to GitHub's search result nature which may have duplicate results on subsequent pages,
    // calling OnConflictStrategy.REPLACE will cause the database to change and adapter to refresh,
    // that will disturb user interface by having the adapter scrolled back.
    // We will use OnConflictStrategy.IGNORE so avoid that refresh.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(users: List<GitHubUser>)

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<GitHubUser>

    @Query(
        "SELECT * FROM user " +
                "INNER JOIN remote_key ON remote_key.id = user.id " +
                "ORDER BY nextKey"
    )
    fun getAllUsersAsPagingSource(): PagingSource<Int, GitHubUser>
}