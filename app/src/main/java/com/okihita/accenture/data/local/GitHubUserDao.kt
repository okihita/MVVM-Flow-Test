package com.okihita.accenture.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.okihita.accenture.data.model.GitHubUser

@Dao
interface GitHubUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUsers(users: List<GitHubUser>)

    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<GitHubUser>

    @Query("SELECT * FROM user WHERE login LIKE :query")
    fun pagingSource(query: String): PagingSource<Int, GitHubUser>
}