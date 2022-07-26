package com.okihita.accenture.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.okihita.accenture.data.model.GitHubUserDetails

@Dao
interface GitHubUserDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(users: GitHubUserDetails)

    @Query("SELECT * FROM user_details WHERE id = :userId")
    suspend fun getUserById(userId: Int): GitHubUserDetails?
}